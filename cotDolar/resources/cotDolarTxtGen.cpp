#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <ostream>
#include <cmath>

using namespace std;

int main(void)
{

	double val = 4, maxDeviance = 0.12, maxD = 0, delta = drand48();
	int days = 365*4+366; // no máximo 2 anos bissextos existem 
	// 2012 também mas fev/2012 não está incluso no trabalho.

	for (uint i = 0; i < days; i++)
	{
		printf("%3.4lf\n", val);
		maxD = val * (maxDeviance * 1.03);
		val *= (1 - (maxDeviance / 2));
		if (val < 1) val = sqrt(val);
		delta = drand48() * maxD;
		val += delta;
	}

	return 0;
}
