mtype = {wantToThrowWaste, liquidsBin, compostBin, 
	plasticOrMetalBin, paperFibreBin, landfillBin, wasteThrown};
chan signal = [0] of {mtype};
bool questionAsked = false;
bool answerReceived = false;
bool usedTheRightBin = false;

active proctype P() {

	personWantsToThrowWaste: atomic {
		printf("One person wants to throw waste\n");
		signal ! wantToThrowWaste;
		goto waitingAnswer;
	};

	waitingAnswer: atomic {
		printf("The person is waiting for the Recycling and Waste Center to answer\n");
		answerReceived = true;
		if
			:: signal ? liquidsBin -> atomic {
					printf("Waste must be trhown in the liquids bin\n");
					signal ! wasteThrown;
			   };
			:: signal ? compostBin -> atomic {
			   		printf("Waste must be thrown in the compost bin\n");
					signal ! wasteThrown;
			   };
			:: signal ? plasticOrMetalBin -> atomic {
					printf("Waste must be thrown in the plastic or metal bin\n");
					signal ! wasteThrown;
			   };
			:: signal ? paperFibreBin -> atomic {
					printf("Waste must be thrown in the paper fibre bin\n");
					signal ! wasteThrown;
			   };
			:: signal ? landfillBin -> atomic {
					printf("Waste must be thrown in the landfill bin\n");
					signal ! wasteThrown;
			   };
		fi;
	};
}


active proctype UBB_RWC() {

	waitingQuestion: atomic {
			signal ? wantToThrowWaste -> {
				printf("The person asked what bin to use\n");
				questionAsked = true;
				if
					:: true -> atomic {signal ! liquidsBin;}
					:: true -> atomic {signal ! compostBin;}
					:: true -> atomic {signal ! plasticOrMetalBin;}
					:: true -> atomic {signal ! paperFibreBin;}
					:: true -> atomic {signal ! landfillBin;}
				fi;
				goto waitingResponse;
			};
	};

	waitingResponse: atomic {
		signal ? wasteThrown -> {
			printf("Waste was thrown in the corresponding bin\n");
			usedTheRightBin = true;
		};
	};
}

/* LTL formulas
	[](!questionAsked-> <> answerReceived)
	[](!questionAsked-> <> usedTheRightBin)
	[](!answerReceived-> <> usedTheRightBin)
*/