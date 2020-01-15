/*
*Author: Sharanya Sudhakar
*System Calls using fork, pipe, execlp, wait, cloase and dup2
*parent process spawns a child that spawns a grand-child that
*spawns a great-grand-child. Each process should execute a different command
*
*Project Part 1.
*/

#include <unistd.h>
#include <sys/wait.h>
#include <stdio.h>
#include <iostream>

using namespace std;

int main(int argc, char *argv[]) 
{

	int status, fd1[2], fd2[2];
	int pid; //use same pid for every fork?

	//fork before initializing pipes.
	if ((pid = fork()) < 0)
		perror("fork error");

	if (pipe(fd1) != 0)
		perror("pipe error");
	if (pipe(fd2) != 0)
		perror("pipe error");

	if (pid == 0)
	{
		if ((pid = fork()) < 0)
			perror("fork error");
		if (pid == 0)
		{
			if ((pid = fork()) < 0)
				perror("fork error");

			if (pid == 0)
			{
				// greatgrandchild
				close(fd1[0]);
				close(fd2[0]);
				close(fd2[1]);
				dup2(fd1[1], 1);
				execlp("ps", "ps", "-A", NULL);
			}
			else
			{
				// grandchild

				close(fd1[1]);
				close(fd2[0]);
				dup2(fd2[1], 1);
				dup2(fd1[0], 0);
				wait(&status);
				execlp("grep", "grep", argv[1], NULL);
			}
		}
		else
		{

			close(fd1[0]);
			close(fd1[1]);
			close(fd2[1]);
			dup2(fd2[0], 0);
			wait(&status);
			execlp("wc", "wc", "-l", NULL);
		}
	}
	else
	{
		wait(&status);
	}
	return 0;
}
