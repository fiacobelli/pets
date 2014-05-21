'''
    My Attempt at LDA Topic Modeling
'''
# list files
import os,random

def gibbs(docs,topics,iterations,alpha,beta,seed):
    ''' The main inputs are w and d (words and doc indices) that contain numbers, where w[i] and d[i] 
        tell me that the ith token is word id w[i] in document id d[i]. That is what getWordsFromFiles(.) does.
    '''
    # This is not exactly part of gibbs, but helps. 
    #It is the corpus as W (tot num of words), D= (tot Num of documents), 
    #w and d where w[i] is the id of the ith token and d[i] is the id of the document where the ith word was found.
    W,D,w,d,types = getWordsFromFiles(docs) 
    ztot = [0]*topics
    Zi = [] #topic assignments
    print len(Zi),len(w),W,D
    CWT = []
    CDT = []
    # initialize CWT and CDT?
    for i in range(W):
        CWT.append([0]*topics)
    for i in range(D):
        CDT.append([0]*topics)
    for i in range(len(w)):
        #print i,j
        #print w[i],d[i]
        topic =  random.randrange(0,topics)
        Zi.append(topic)
        CWT[w[i]][topic] +=1
        CDT[d[i]][topic] +=1
        ztot[topic]+=1
   # print CDT    # Now iterate
    print "Before we start:",CWT[0]
    for it in range(0,iterations):
        print "Iteration %s"%it
        for i in range(len(w)):
            topic = Zi[i]
            CWT[w[i]][topic] -=1
            CDT[d[i]][topic] -=1
            prob = [0 for x in range(0,topics)]
            for topic in range(topics): 
                prob[topic] = (float(CWT[w[i]][topic] + beta)/float(ztot[topic]+float(W)*beta))*(float(CDT[d[i]][topic]+alpha)/float(sum(CDT[d[i]])+topics*alpha))
            totProb = sum(prob)
            ztot[topic] -=1
            #sample from distribution (which is nultinomial)
            # To sample from a multinomial distribution we use an auxiliary variable X from the uniform distribution (0,1).
            # Read more in Wikipedia (binomila dist).
            x = random.random()*totProb
            #print totProb,x,prob
            maxP = prob[0]
            topic = 0
            #print x
            while x>maxP:
                topic +=1
                maxP += prob[topic]
            # new topic assignment.
            Zi[i] = topic
            ztot[topic]+=1
            CWT[w[i]][topic] +=1
            CDT[d[i]][topic] +=1
            
        print len(CWT),CWT[0],CWT[1]
    return (CWT,CDT,Zi,types)
    

def getWordProbs(CWT,types,top):
    typeIdx ={}
    for k,v in types.iteritems():
        typeIdx[v] = k
    topics = len(CWT[0])
    totals=[]
    probs=[]
    for j in range(0,topics):
        k = [word[j] for word in CWT]
        #print k[:10]
        totals.append(sum(k))
        prob = [(idx,float(p)/(totals[j]+0.0001)) for idx,p in enumerate(k)]
        prob.sort(key=lambda x:x[1])
        prob.reverse()
        probs.append(prob[:top])
    # Print
    for t,pt in enumerate(probs):
        print "Topic %s"%t
        for idx,prob in pt:
            print typeIdx[idx],prob
    return probs
    
    
def getWordsFromFiles(docs,stoplistFile="stoplist.txt"):
    ''' docs is a list of files'''
    texts = [ open(x).read() for x in docs]
    stoplist = open(stoplistFile).read().split()
    return getWordsAndDocs(texts,stoplist)
    
def getWordsAndDocs(texts,stoplist=[]):
    words = []
    types = {}
    totTypes = 0
    d = []
    w = []
    for i,doc in enumerate(texts):
        tokens = doc.split()
        for token in tokens:
            token.replace(".","").replace(",","").replace(")","").replace("(","").replace("--","").replace("?","").replace(":","").lower()
            if token not in stoplist:
                words.append(token)
                d.append(i)
    for j,token in enumerate(words):
        if types.has_key(token):
            w.append(types[token])
        else:
            types[token]=totTypes
            w.append(totTypes)
            totTypes +=1
    # Save the word index
    f=open("wordIdx.idx","w")
    for k,v in types.iteritems():
        f.write("%s:%s\n"%(k,v))
    f.close()
    return (len(types.keys()),len(docs),w,d,types)


            
if __name__=='__main__':
    import sys
    basedir = sys.argv[1]
    docs = [os.path.join(basedir,x) for x in os.listdir(basedir) if x.endswith("txt")]
    print len(docs), "documents will be processed"
    CWT,CDT,Zi,types = gibbs(docs,5,100,0.05,0.001,1)
    wp = getWordProbs(CWT,types,20)
    f=open(sys.argv[2],"w")
    for w in wp:
        f.write("%s:%s\n"%(w[0],w[1]))
    f.close()
    
