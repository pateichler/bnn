# BNN

BNN (Brain-like neural network) was the idea to use graph cellular automata (GCA) to create a recurrent neural network (RNN). In the network, there are designated input, hidden, and output neurons. The goal is to feed data to the input neurons and get a desired output from the output neurons (such as the case in a RNN).

## Usage

Warning, this project was in the very early prototyping stages with no current intentions of carrying forward. Nonetheless, check out [bnn-world](https://github.com/pateichler/bnn-world), if you would like to run the genetic evolution program.

## Motivation

The motivation for the project was from an attempt to combine the behavior of neurons and machine learning techniques to create a new model of AI. The rough model is outlined below:

Data is fed to input neurons by simple binary activation. Neurons are activated when incoming excitatory neurotransmitters reach a certain threshold. Once a neuron is activated, it sends neurotransmitters to all connected neurons. The neurotransmitters sent can cause a excitatory, inhibitory, or modulatory response on the neuron. The number of each neurotransmitter sent depends on the strength of the connection.

The strength of each connection is constantly updated by the state of the neuron. The state of each neuron is updated by the states of connected neurons (such as the case with GCA). 

The changes in neuron state and connection strength are both calculated using weights encoded in the neuron's "genetics". The genetics for the neurons can be optimized by using the genetic algorithm. The hope is that the genetics will slowly evolve to produce GCA that can trigger the right output neuron based on the input.

In order to help train the network while growing, the correct output neurons are fed modulatory neurotransmitters to indicate the correct output.

There are many possible models that this outline could be implemented. So far, all the models tested in this project has had no success.

## Future

The vision of the project is to have a fully recurrent neural network that is evolved to learn any task. The unrealistic vision is similar to the work in this project. The main reason for project failure was because the all the ideas were "Hail Mary" ideas with little academic backing or motivation, so take all the ideas presented in the project with a grain of salt. If I return to the project, I would like to ground my ideas in actual research as a starting point.
