#include "NeuralNetwork.h"

#include <cstdlib>
#include <ctime>
#include <iostream>

NeuralNetwork::NeuralNetwork(int nI, int nH, int nO) : nInput(nI), nHidden(nH), nOutput(nO)
{
    srand( (unsigned int) time(0) );

	//create neuron lists
	//--------------------------------------------------------------------------------------------------------
	inputNeurons = new( double[nInput + 1] );
	for ( int i=0; i < nInput; i++ ) inputNeurons[i] = 0;

	//create input bias neuron
	inputNeurons[nInput] = -1;

	hiddenNeurons = new( double[nHidden + 1] );
	for ( int i=0; i < nHidden; i++ ) hiddenNeurons[i] = 0;

	//create hidden bias neuron
	hiddenNeurons[nHidden] = -1;

	outputNeurons = new( double[nOutput] );
	for ( int i=0; i < nOutput; i++ ) outputNeurons[i] = 0;

    nTotal = nInput + nHidden + nOutput;

	//create weight lists (include bias neuron weights)
	//--------------------------------------------------------------------------------------------------------
	//conns = new( double*[nTotal] );
	conns = * new std::vector < std::vector<Connection> >();
	for ( int i=0; i <= nTotal; i++ )
	{
		conns.push_back( *new std::vector<Connection>() );
		for ( int j=0; j < nTotal; j++ ) {
            conns[i].push_back({j,0});
		}
	}

    energies = new( double[nTotal] );
	for ( int i=0; i <= nTotal; i++ )
	{
        energies[i] = 0;
	}
	initWeights();
}
void NeuralNetwork::initWeights()
{
    for (int i=0; i<nTotal; ++i){
        for (int j=0; j<nTotal; ++j){
            if (i!=j){
                conns[i][j] = ( (double)(rand()%100)+1)/100;
                conns[i][i] += conns[i][j];
            }
        }
    }
}
double* NeuralNetwork::read()
{
    double value = (energies[nTotal - nOutput]);
    energies[nTotal - nOutput] = 0;
    return &value;
}

void NeuralNetwork::write(double* input)
{
    for (int i=0; i<nInput; ++i){
        energies[i] = input[i];
        for (int j=0; j<nTotal; ++j){
            if (i!=j){
                energies[j] += (conns[i][j]/conns[i][i])*energies[i];
            }
        }
        energies[i] = 0;
    }

    for (int i=nInput; i<nTotal-nOutput-1; ++i){
        for (int j=0; j<nTotal; ++j){
            if (i!=j){
                energies[j] += (conns[i][j]/conns[i][i])*energies[i];
            }
        }
        energies[i] = 0;
    }
}

void NeuralNetwork::teach(double reenforcement)
{
    for (int i=0; i<nInput; ++i){
        for (int j=0; j<nTotal; ++j){
            if (i!=j){
                double adjustFactor = ( (reenforcement)*( ((double)rand() / (double)RAND_MAX)*2 - 1 ) );
                conns[i][j] += adjustFactor;
                conns[i][i] += adjustFactor;
            }
        }
    }

    for (int i=nInput; i<nTotal-nOutput; ++i){
        for (int j=0; j<nTotal; ++j){
            if (i!=j){
                double adjustFactor = ( (reenforcement)*( ((double)rand() / (double)RAND_MAX)*2 - 1 ) );
                conns[i][j] += adjustFactor;
                conns[i][i] += adjustFactor;
            }
        }
    }
}


NeuralNetwork::~NeuralNetwork()
{
    //dtor
}
