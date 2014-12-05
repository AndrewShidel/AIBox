#ifndef NEURALNETWORK_H
#define NEURALNETWORK_H

#include <vector>

class NeuralNetwork
{
    public:
        NeuralNetwork(int nI, int nH, int nO);
        virtual ~NeuralNetwork();

        double* read();
        void write(double* input);
        void teach(double reenforcement);
    private:
        //neurons
        double* inputNeurons;
        double* hiddenNeurons;
        double* outputNeurons;

        //number of neurons
        int nInput, nHidden, nOutput, nTotal;

        //weights
        //double** conns;

        typedef struct {
            int index;
            double weight;
        } Connection;

        std::vector < std::vector<Connection> > conns;

        //energies
        double* energies;

        void initWeights();
};



#endif // NEURALNETWORK_H
