#########################################################################################
##        Random projection forests for kNNs                                           ##    
##                                                                                     ##
## The method is based on the ensemble of enhanced random projection trees             ##
##                                                                                     ##
## Author:  	Donghui Yan, Jin Wang, Zhenpeng Li, Honggang Wang                      ##
## Version 0:	06/15/2016                                                             ##
## Version 1:	09/22/2017							       ##
##				Selection of random projections                        ##
## Version 2:   10/12/2017							       ##
##				Selection of split point for given projection	       ##
##                                                                                     ##
#########################################################################################
library(MASS);
#Default setting
K<-5;
T<-40; ##Number of RP trees
NN<-30; ##Max size of a tree leaf
nTry<-5;
##
## wNodes
## Double queue to maintain the set of active tree nodes
## When flag is set to 1 that means no further work on this node
## so such a node is actually a leaf node
##
##   idxs ----> n_idx  | size  | childL | nL | childR | nR | flag
##              n_idx  | size  | childL | nL | childR | nR | flag 
##              .....  | ....  | ...... | ...| ...... | ...| ....
##              n_idx  | size  |  childL| nL | childR | nR | flag
##              .....  | ....  | .......| ...| ...... | ...| ....
##   idxe ----> n_idx  | size  |  childL | nL| childR | nR | flag
##
## where idxw points to current working node, n_idx points to the start of a tree node,
## size indicating # data points in this node. childL/R are the index into the original
## data table for child nodes. 
##
## rProjs
## idxs | idxL | idxR | s | rProj
## idxs | idxL | idxR | s | rProj
## .... | .... | .... | ..| .....
## idxs | .... | .... | ..| .....
##
## where idxL and idxR are indices into this table for child nodes.
##
## Another structure maintains the record number of all points in respective tree nodes
## recs
##
##   n_idx ---> rec #1                      n_idx ---> rec #1
##              rec #2                                 ......
##              ......                                 ......
##              ......                      n_idx ---> ......
##              ......      =====>                     ......
##              ......                                 ......
##   n_idx ---> rec #i                      n_idx ---> rec #i
##              ......                                 ......
##              rec #n                                 rec #n
##
## When a node is split, its entries of recs will be replaced by that of the child nodes 
## Convention: left child node placed on top and right bottom.
##
rpTree<-function(x)
{
	
n<-nrow(x); p<-ncol(x);
idxs<-1; idxe<-1; 
recs<-matrix(0,n,3); recs[,1]<-1:n; recs[,2]<-1; recs[,3]<-1;
wNodes<-matrix(0,1,8); wNodes[1,1]<-1; wNodes[1,2]<-n; wNodes[1,7]<-0;
wNodes[1,8]<-1;

while(1)
{
	
##All nodes processed so stop
if(idxs > idxe) { break;}

##To pick a node from the working set
n_idx<-wNodes[idxs,1]; nSize<-wNodes[idxs,2];
nFlag<-wNodes[idxs,7];
code<-wNodes[idxs,8];

##Leaf node so move to the next active node
if(nFlag == 2) { idxs<-idxs+1; next;}

##Too small a node, treat as a leaf node
if(nSize < NN) { wNodes[idxs, 7]<-2; idxs<-idxs +1; 
	##rProjs<-cbind(rProjs, c(0,0,0,rep(0,p))); 
	next;}

##To retrieve the node
idxx<-recs[n_idx: (n_idx+nSize-1),1];
z<-x[idxx,1:p];

##To generate nTry random directions and choose one with largest spreadout
sdProj<-0; 
for(j in 1:nTry)
{
	theta<-matrix(rnorm(p,0,1),p,1);
	projt<-z %*% theta;
	if(sd(projt)>sdProj) { proj<-projt; sdProj<-sd(projt);}
}

##To generate a random split point
##s<-runif(1, min=min(proj), max=max(proj));
##2).
##minn<-min(proj); maxx<-max(proj);
##s<-runif(1, min=minn+(maxx-minn)/3, max=maxx- (maxx-minn)/3);
##3).
s<-median(proj);


##To split at where the max gap is
if(F){
tmp<-sort(proj);
stmp1<-tmp[1:(nSize-1)]; stmp2<-tmp[2:nSize];
gaps<-stmp2-stmp1; pidx<-which.max(gaps);
s<-tmp[pidx+1];
}


##To split at the max gap among 10 selected points
if(F){
NS<-10;
ss<-runif(NS, min=min(proj), max=max(proj));

gap<-0;pidx<-0;
for(j in 1:NS)
{
	tmp<-proj-ss[j];
	gapt<-min(tmp[tmp>0]) - max(tmp[tmp<=0]);
	if(gapt >gap) { gap<-gapt; pidx<-j;}
}
s<-ss[pidx];
}##End of if(F)



##Split the node
##That is, to replace the record entries of a node with that of its child nodes
tmpL<-(1:nSize)[proj < s]; tmpR<-(1:nSize)[proj >= s]; 
childL<-idxx[tmpL]; childR<-idxx[tmpR]; 

##Added by dhyan on 09/13/2017
if(length(tmpL)==0 || length(tmpR)==0) 
{ 	wNodes[idxs, 7]<-2;
	##idxs<-idxs+1; 
	##cat("--------length(tmpL)=", length(tmpL), "length(tmpR)=", length(tmpR),"\n");
	next;
}


nL<-length(childL); nR<-nSize-nL;
recs[n_idx:(n_idx+nL-1),1] <- childL; 
recs[n_idx:(n_idx+nL-1),2] <- nL;
recs[n_idx:(n_idx+nL-1),3] <- code*2;
recs[(n_idx+nL): (n_idx+nSize-1),1] <- childR; 
recs[(n_idx+nL): (n_idx+nSize-1),2] <- nR;
recs[(n_idx+nL): (n_idx+nSize-1),3] <- code*2+1;

##Add the child nodes to its parent
wNodes[idxs, 3:8]<-c(n_idx, nL, (n_idx+nL), nR, 1, code);


##Add the child nodes into the working set
idxe<-idxe+1; idxL<-idxe;
wNodes<-rbind(wNodes,c(n_idx, nL, 0,0,0,0,0, code*2));
idxe<-idxe+1;
wNodes<-rbind(wNodes,c(n_idx+nL, nR,0,0,0,0,0, code*2+1));

##Point to the next working node
idxs<-idxs+1;
}

recs
}##End of function rpTree()




#############################################################################
##                                    					    #
##     knnb								    #
##                                                                          #
## To compute the pairwise distances for points in the same leaf node, and  #
## keep only the K nearest ones for each point.                             #
##                                                                          #
#############################################################################
knnb<-function(x, idxs)
{
	
	n<-nrow(x); p<-ncol(x);
	dist<-matrix(0,n,n);
	whichkb<-matrix(0,n,K);
	distkb<-matrix(0,n,K);
		
	for(j in 1:p)
	{
		tmp<-kronecker(x[,j],t(x[,j]),FUN="-");
		dist<-dist+tmp*tmp;
	}
	
	for(i in 1:n)
	{
		tmp<-sort(dist[i,], index.return=TRUE);
		whichkb[i,]<-idxs[tmp$ix[2:(K+1)]];
		distkb[i,]<-tmp$x[2:(K+1)];
	}
	
	##This implementation is slower than R function sort()
	if(F){
	for(i in 1:n)
	{
		tmp<-topK(-dist[i,],1:length(dist[i,]),K);		
		whichkb[i,]<-idxs[tmp$which[2:(K+1)]];
		distkb[i,]<-tmp$values[2:(K+1)];
	}
	}

	z<-NULL;
	z$whichkb<-whichkb;
	z$distkb<-distkb;
	z
}


######################################################################
## 
## To find the kNNs among the collection of bins in the ensemble
##
######################################################################
knnbMerge<-function(whichk, distk)
{
	
	whichK<-matrix(0,1,K); distK<-matrix(0,1,K);
	
	tmp<-sort(distk, index.return=TRUE);
	whichks<-whichk[tmp$ix];
	distks<-distk[tmp$ix];

	i<-1; k<-1; last<-0;
	while(1)
	{
		if(whichks[i] != last)
		{
			whichK[k] <- whichks[i];
			distK[k] <- distks[i];
  			last<-whichks[i];
			k<-k+1;
		}
		i<-i+1;
		if(k > K) break;
	}
	z<-NULL;
	z$whichK<- whichK;
	z$distK <- distK;
	z	
}




#############################################################
#
#############################################################
knn<-function(x)
{

##T<-3;
n<-nrow(x); p<-ncol(x);

whichks<-matrix(0,n,K*T); distks<-matrix(0,n,K*T);
whichK<-matrix(0,n,K); distK<-matrix(0, n,K);

pvals<-matrix(0,1,T);

for (tt in 1:T)
{
##if(10*floor(tt/10)==tt) cat("  =========> t = ",tt," @", date(),"\n");

recs<-rpTree(x);
cat("  tt=", tt, ", Finshed one rpTree() @", date(), "\n");
ptrL<-(tt-1)*K+1; ptrR<-tt*K;

idx<-1;
while(1)
{
	nSize<-recs[idx,2];
	idxs<-recs[idx:(idx+nSize-1),1]
	xn<-x[idxs,];
	tmp<-knnb(xn, idxs);
	whichks[idxs,ptrL:ptrR]<-tmp$whichkb;
	distks[idxs,ptrL:ptrR]<-tmp$distkb;

	idx<-idx+nSize;
	if(idx>n) break;	
}##End of while(1)
cat("    Finished computing kNNs for a bin @", date(),"\n");
}##End of for(tt in 1:T) loop

##cat("	Begin padding all kNNs from all bins @", date(),"\n");
for(i in 1:n)
{
	idxz<-(1:(K*T))[!duplicated(whichks[i,])];
	tmp<-knnbMerge(whichks[i,idxz], distks[i,idxz]);
	##tmp<-knnbMerge(whichks[i,], distks[i,]);
	whichK[i,]<-tmp$whichK; distK[i,]<-tmp$distK;
}

z<-NULL;
z$whichK<-whichK;
z$distK<-distK;
##cat("	Finished padding all kNNs @", date(),"\n");
z
}##End of fundtion knn()


##############################################################################
# Return the top k entries of a list
##############################################################################
topK<-function(x,indices,k)
{
    n<-length(x);
    l<-sample(1:n,1);
    pivot<-x[l];
	topKs<-NULL;
    
    if(n<k | n==k) 
	{ topKs$values<-x; topKs$which<-indices[1:n]; return(topKs);}
    
    idx<-c((1:n)[x>pivot],(1:n)[x==pivot]);
    m<-length(idx);
    if(m==n) 
	{ 
		if(max(x) == min(x)) 
		{ topKs$values<-x[1:k]; topKs$which<-indices[idx[1:k]]; 
			return(topKs);}
	}
	
    if(m>k) { 	return(topK(x[idx],indices[idx],k));}
    else if(m==k) 
	{ topKs$values<-x[idx]; topKs$which<-indices[idx]; return(topKs);}
    else
    {
        zL<-topK(x[-idx],indices[-idx],(k-m));
		topKs$values<-c(x[idx],zL$values); 
		topKs$which<-c(indices[idx], zL$which); 
		return(topKs);
    }
}##End of topK()





###########################################################################
##Data 1
##if(F){
data(iris);
n<-nrow(iris); p<-ncol(iris)-1;
x<-matrix(0,n,p); for(i in 1:p) { x[,i]<-iris[,i];}
##}

##Data 2 (Wisconsin breast cancer (diagnostic, 569 x 30))
## 	#Trees		AvgMissCnts		AvgMissDist
##	10			0.008787346		1.515441
##	20			0				0
##	40			0				0	
##	60			0				0
##	80			0				0
##	100			0				0
if(F){
tmp<-read.table("wdbc.data",sep=",");
rmvs<-c(1,2);
tmp<-tmp[,-rmvs]; ##To exclude the class ID
n<-nrow(tmp); p<-ncol(tmp);
x<-matrix(0,n,p); for(i in 1:p) { x[,i]<-tmp[,i];}
}




##To find out how many true kNNs missed in the approximate computing
## (Average computed over 100 repetitions)
RUNS<-1;
avgMissCnts<-matrix(0,RUNS,1);
avgDiffKNNd<-matrix(0,RUNS,1);
avgKNNdA<-matrix(0,RUNS,1);
avgKNNdB<-matrix(0,RUNS,1);
T<-20;NN<-20;nTry<-3;K<-5;
for(iter in 1:RUNS)
{
missCnts<-matrix(0,n,1);
diffKNNd<-matrix(0,n,1);
KNNdA<-matrix(0,n,1);
KNNdB<-matrix(0,n,1);
if( iter %% 10 ==0) cat("====== run =",iter,"@ ", date(),"======\n");
cat("----> Start @", date(),"\n");
xknn<-knn(x);
cat("----> Finish @", date(),"\n");


##cat("----> Start @", date(),"\n");
##p<-ncol(x);
Infinity<-10^10;
##if(F){
xknnV<-matrix(0,n,K); xknnW<-matrix(0,n,K);
for(i in 1:n)
{
	if( i %% 1000 ==0) cat("====== i=",i,"@ ", date(),"======\n");
	point<-x[i,];
	zz<-sweep(x,2,point, '-');
	dist<-rowSums(zz^2);
	dist[i]<-Infinity;
	zz<-topK(-dist,1:n,K);
	xknnV[i,]<-(-1)*zz$value; xknnW[i,]<-zz$which;
}
##cat("<----- Finish @",date(),"\n");
##save(xknnV, file="xknnV.Rdata");
##save(xknnW, file="xknnW.Rdata");
##}
##load("xknnV.Rdata"); load("xknnW.Rdata");
##xknnV<-read.table("xknnV.Rdata", sep=","); xknnV<-as.numeric(xknnV);
##xknnW<-read.table("xknnW.Rdata",sep=","); xknnW<-as.numeric(xknnW);

zz<-cbind(xknn$whichK, rep('*',n), xknnW)
zz
zzd<-cbind(xknn$distK, rep('*',n), xknnV)
zzd
##To find out how many true kNNs missed in the approximate computing
##Note further care is needed to handle the case of ties, i.e., it may 
##happen that several points are all the k-th nearest neighbors, but 
##not all are returned as kNNs (as we only need k points in kNNs). The
##current calculation to evaluate missed counts overestimate the errors   
##a little bit.
##
for(i in 1:n)
{
	missCnts[i]<-K-length(intersect(xknn$whichK[i,], xknnW[i,]));
	##missCnts[i]<-K-length(intersect(xknn$distK[i,], xknnV[i,]));
	diffKNNd[i]<-abs(sqrt(max(xknn$distK[i,])) - sqrt(max(xknnV[i,])));
	##diffKNNd[i]<-sum((sort(xknn$distK[i,]) - sort(xknnV[i,]))^2);
	KNNdA[i]<-max(xknn$distK[i,]);
	KNNdB[i]<-max(xknnV[i,]);
}
avgMissCnts[iter]<-mean(missCnts);
avgDiffKNNd[iter]<-mean(diffKNNd);
avgKNNdA[iter]<-mean(KNNdA);
avgKNNdB[iter]<-mean(KNNdB);
}
cat("\n K=",K,"nTry=",nTry,"@",date(),"\n");
cat("# rpTrees in ensemble =", T, "\n");
cat("Avg missed counts =", mean(avgMissCnts), "\n");
cat("Avg kNN distance =", mean(avgDiffKNNd), "\n");


