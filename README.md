[![CI](https://img.shields.io/github/workflow/status/webis-de/sigir-sampling-bias-ltr/CI?style=flat-square)](https://github.com/webis-de/sigir-sampling-bias-ltr/actions?query=workflow%3ACI)
[![Issues](https://img.shields.io/github/issues/webis-de/sigir-sampling-bias-ltr?style=flat-square)](https://github.com/webis-de/sigir-sampling-bias-ltr/issues)
[![License](https://img.shields.io/github/license/webis-de/sigir-sampling-bias-ltr?style=flat-square)](LICENSE)

# sigir-sampling-bias-ltr

This repository contains data and source code for reproducing results of the paper:  
**Sampling Bias Due to Near-Duplicates in Learning to Rank**

The source code is released under terms of the [MIT License](LICENSE).

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
Literature links:
[Webis publications](https://webis.de/publications.html?q=Sampling+Bias+Due+to+Near-Duplicates+in+Learning+to+Rank), 
DBPL, 
DOI

## Usage

_TODO_

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
