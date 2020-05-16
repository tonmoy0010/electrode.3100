# electrode.3100

This is a computing project to develop job distributing system. The programming language used was Java.

###### STAGE 2 DEMO INSTRUCTIONS

#####  PRE-CONDITION:
	- We need to have the files from ds-sim under one folder.
	- Run command make.
	- There should be a runnable server file created.

##### STEPS TO PRODUCE FOR DEMO:
	1. Turn on terminal.
	2. Make sure you are inside the directory where the demo is going to be run from
	3. Run the command "javac Client.java" too compile the Java file.
	3. Run the server using the relevant command.
	4. On a separate terminal and head to the current directory of program and run the server using:
		a) ./ds-server -c <config_file> -v all"
	4. Run the client using "java Client -a <fit_algorithm>". This will allow you to select algorithms.
	   Only First Fit and Worst Fit in this demo.
		a) fit algorithm commands are:
		 	ff - firstFit
		 	wf - worstFit
	5. Observe the output in the server terminal and compare values.


