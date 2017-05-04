
## Latent Dirichlet Allocation (Topic Modeling)

If we assume documents are comprised of different topics, then we can assume that certain words belong to certain topics. Therefore, a document is a set of words that are more or less grouped according to their topic. For example: A sports story can have a few topics in it such as "the actual game" (score, plays, etc.), "the players" (injuries, drug testing, etc.) and "speculation" (what will happen against the next rival). For each of these topics there are words that are strongly associated with them. For example: "score" is strongly associated to "the actual game" topic, while "steroids" is probably associated to "the players" topic. However, "score" may also be associated with the "speculation" topic albeit less strongly.

Because words are associated with topics with relative "strengths" we can talk about the probabilities with which each words is associated to each topic. Similarly we can talk about the probability that a document contains each topic.

Now, When we don't know the topics a priori nor the words associated with them nor the topics contained in a document, but we know the words in each document, we need to infer the topical structure of documents. For this we need a generative model. A model that can infer topics by trying to generate the topics in the documents by iterating and assigning topics to words that occur together. LDA is one such model. Perhaps the most popular. You can find a lot more on David Blei's Topic Modeling webpage

There are several LDA implementations. This project is my take on it (because I've had issues with the others in one aspect or another).

DOWNLOAD: Just download these java classes where you can run them. You must have Java 1.6+ installed (OpenJDK is fine). This is a collection of three java programs. They need to be run by typing:

    java <Name of the program> <Options>
		 

The programs are:

* Corpus: Which creates a corpus from a directory of text files
* LdaGibbs: which runs LDA on a corpus
* LdaAnalysis: which can create a corpus, run LDA and save the results. It can also run from an already created corpus.

These programs work well, but interfacing with them is in its early stages, so let me know how can I make them nicer. Of course, bugs and oddities are welcome feedback.

f-iacobelli [at] neiu[dot] edu 
