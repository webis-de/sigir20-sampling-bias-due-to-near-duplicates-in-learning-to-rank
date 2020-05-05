# Feature datasets

## ClueWeb 09 features

The features required to run our experiments on the ClueWeb09 corpus
are already included [in this Git repository](ClueWeb09).
Simply clone with [Git LFS](https://git-lfs.github.com/) enabled.

Our dataset is derived from  judged documents 
of the TREC 2009–2012 Web Tracks,
by computing text-based (body, title, anchors, main content), and web-graph based features:

| Feature | Count |
|---|---|
| Term frequency | 4 |
| TF · IDF | 4 |
| BM25 score | 4 |
| F2 exp score | 4 |
| F2 log score | 4 |
| QL score | 4 |
| QLJM score | 4 |
| PL2 score | 4 |
| SPL score | 4 |
| URL length | 1 |
| No. of slashes in URL | 1 |
| PageRank | 1 |
| SpamRank | 1 |
| No. of inlinks | 1 |
| No. of outlinks | 1 |

As we only compute features for judged documents, 
our dataset is suited for supervised learning.

### Versions

We have different feature versions,
similar to LETOR supervised learning features.

#### Null version – [`NULL.txt`](ClueWeb09/NULL.txt)
For some documents we can't provide all features.
We use `NaN` to indicate unknown / minus infinity values.
This data cannot be directly be used for learning.

#### Min version – [`min.txt`](ClueWeb09/min.txt)
Replace the `NaN` value in _Null_ version with the minimal value 
of this feature under a same query. 
This data can be directly used for learning.

#### Query-level norm version – [`Querylevelnorm.txt`](ClueWeb09/Querylevelnorm.txt)
Conduct query level normalization based on data in _Min_ version. 
This data can be directly used for learning.

### Partitions
We further provide 5-fold partitions for cross fold validation ([`Fold1`](ClueWeb09/Fold1), …, [`Fold5`](ClueWeb09/Fold5)),
as well as a partition that contains the queries with the highest amount of
near-duplicate documents ([`MostRedundantTraining`](ClueWeb09/MostRedundantTraining)).

### License

Our features dataset is licensed under terms of the [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/) license.

## LETOR features

If you want to run our experiments on the GOV2 corpus, please download the [LETOR 4.0 dataset](https://www.microsoft.com/en-us/research/project/letor-learning-rank-information-retrieval/#!letor-4-0), and unpack the `MQ2007`, and `MQ2008` folders to `data/features/MQ2008`, or `data/features/MQ2007` respectively: