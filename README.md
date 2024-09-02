# BNN

BNN is an attempt for a brain-like recurrent neural network. The idea is to make a recurrent neural network that uses cellular automata to update the weights. The automata rules are encoded into the genes, so it can be learned using the genetic algorithm.

There are many possible models that this outline could be implemented. So far, all the models tested in this project has had no success. Outlined below is some of the ideas tested for implementing the genes of the cellular automata as well as the information available to the cellular automata.

## Usage

Warning, this project was in the very early prototyping stages with no current intentions of carrying forward. Nonetheless, check out [bnn-world](https://github.com/pateichler/bnn-world), if you would like to run the genetic evolution program.

## Motivation

Adjusting weights in a RNN is commonly done through backpropagation, but it isn't a perfect solution for all types of learning. The goal of this project was to use cellular automata to adjust the neuron weights. Then the cellular automata rules will be learned through the genetic algorithm.

In model 2, I choose the rules for the cellular automata to be represented by a small neural network. The inputs of the cellular automata are the states of the connected neurons represented as neurotransmitters. The strength of connection between two neurons changes based on the two states of the neurons. This is represented in the genetics by a look-up table with the rows being the state of neuron 1 and columns state of neuron 2.

## Future

The vision of the project is to have a fully recurrent neural network that is evolved to learn any task. The unrealistic vision is similar to the work in this project. The main reason for project failure was because the all the ideas were "Hail Mary" ideas with little academic backing or motivation, so take all the ideas presented in the project with a grain of salt. If I return to the project, I would like to ground my ideas in actual research as a starting point.
