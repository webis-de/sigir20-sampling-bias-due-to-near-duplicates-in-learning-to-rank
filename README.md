[![CI](https://img.shields.io/github/workflow/status/webis-de/sigir-sampling-bias-ltr/CI?style=flat-square)](https://github.com/webis-de/sigir-sampling-bias-ltr/actions?query=workflow%3ACI)
[![Issues](https://img.shields.io/github/issues/webis-de/sigir-sampling-bias-ltr?style=flat-square)](https://github.com/webis-de/sigir-sampling-bias-ltr/issues)
[![License](https://img.shields.io/github/license/webis-de/sigir-sampling-bias-ltr?style=flat-square)](LICENSE)

# Sampling Bias Due to Near-Duplicates in Learning to Rank

This repository contains data and source code for reproducing results of the paper:  
_Sampling Bias Due to Near-Duplicates in Learning to Rank_ from [SIGIR 2020](https://sigir.org/sigir2020/)


## Usage


#### Clone

Clone this repository:  
```shell script
git clone https://github.com/webis-de/sigir-sampling-bias-ltr.git
```
We recommend to enable [Git LFS](https://git-lfs.github.com/)
for best performance.


#### Build

Ensure, the project builds on your machine:

```shell script
./gradlew build
```


#### Run experiments

Run ranking experiments with Gradle
(needs access to a configured [Yarn](http://hadoop.apache.org/docs/stable/hadoop-yarn/hadoop-yarn-site/YARN.html) cluster):

```shell script
./gradlew runClueWeb09TrainingRerankingExperimentsSpark
```

<details><summary>GOV2</summary>

```shell script
./gradlew runGov2TrainingRerankingExperimentsSpark
```

</details>

To test your configuration, you may run a small subset 
of the experiments locally:

```shell script
./gradlew runClueWeb09TrainingRerankingExperiments
```

<details><summary>GOV2</summary>

```shell script
./gradlew runGov2TrainingRerankingExperiments
```

</details>


## Datasets

Our ClueWeb 09 features dataset can be found [here](data/features).


## Reference

```bibtex
@InProceedings{webis:2020d,
  author =    {Maik Fr{\"o}be and 
               Janek Bevendorff and 
               {Jan Heinrich} Reimer and 
               Martin Potthast and 
               Matthias Hagen},
  booktitle = {43nd International ACM Conference on Research and Development 
               in Information Retrieval (SIGIR 2020)},
  month =     jul,
  publisher = {ACM},
  site =      {Xi'an, China},
  title =     {{Sampling Bias Due to Near-Duplicates in Learning to Rank}},
  year =      2020
}
```

_Literature links:
[Webis publications](https://webis.de/publications.html?q=Sampling+Bias+Due+to+Near-Duplicates+in+Learning+to+Rank)_


## Support

If you hit any problems reproducing our study, 
please mail us:

- [jan.reimer@student.uni-halle.de](mailto:jan.reimer@student.uni-halle.de)
- [maik.froebe@informatik.uni-halle.de](mailto:maik.froebe@informatik.uni-halle.de)

We're happy to help!


## License

The source code is released under terms of the [MIT License](LICENSE).  
Our features dataset is licensed under terms of the [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/) license.


## Abstract

Learning to rank (LTR) is the de facto standard for web search, 
improving upon classical retrieval models 
by exploiting (in)direct relevance feedback 
from user judgments, interaction logs, etc. 
We investigate for the first time the effect of a sampling bias 
on LTR models due to the potential presence of near-duplicate web pages 
in the training data, and how (in)consistent relevance feedback 
of duplicates influences an LTR model's decisions. 
To examine this bias, we construct a series of specialized LTR datasets 
based on the ClueWeb09 corpus with varying amounts of near-duplicates. 
We devise worst-case and average-case train/test splits 
that are evaluated on popular pointwise, pairwise, and listwise LTR models. 
Our experiments demonstrate that duplication causes overfitting 
and thus less effective models, making a strong case for the benefits 
of systematic deduplication before training and model evaluation.
