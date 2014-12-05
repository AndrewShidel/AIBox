#include <iostream>
#include <cmath>

#include "NeuralNetwork.h"

using namespace std;

int main()
{
    double *input = new (double[2]);
    NeuralNetwork n = *new NeuralNetwork(2, 30, 1);
    input[0] = 10;
    input[1] = 5;
    while (true){
        n.write(input);
        double output = n.read()[0];
        if ( std::abs(output-15) < .1 ){
            std::cout << "It worked!!!\n";
            return 0;
        }else{
            std::cout << "Nothing... " << output << "\n";
            n.teach( std::abs(output-15) );
        }
    }

    return 0;
}
