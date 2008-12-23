/*
 * alloctest.c
 *
 *  Created on: Oct 27, 2008
 *      Author: ebaron
 */
#include <stdlib.h>
#include <unistd.h>

int main(int argc, char **argv) {
	int bytes;
	if (argc > 1) {
		bytes = atoi(argv[1]);
	}
	else {
		bytes = 40;
	}
	int *foo();
	void bar(int *);
	int *ptr1 = (int *)malloc(bytes);
	int *ptr2 = foo();
	int *ptr3 = (int *)malloc(bytes);
	int *ptr4 = foo();
	int *ptr5 = (int *)malloc(bytes);
	int *ptr6 = foo();

	free(ptr1);
	bar(ptr2);
	free(ptr3);
	bar(ptr4);
	free(ptr5);
	bar(ptr6);
	return 0;
}

int *foo(int bytes) {
	return (int *)malloc(bytes);
}

void bar(int *ptr) {
	free(ptr);
}
