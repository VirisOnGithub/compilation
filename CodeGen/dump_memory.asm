dump_memory:
	XOR R0 R0 R0	        # i = 4096
	ADDi R0 R0 4096
	XOR R1 R1 R1
	ADDi R1 R1 ESPACE
	XOR R2 R2 R2
	ADDi R2 R2 SAUT_LIGNE
	XOR R3 R3 R3
	ADDi R3 R3 4500
debut_dump:
	JEQU R0 R3 fin_dump
	LD R4 R0
	PRINT R4
	OUT R1
	ADDi R0 R0 1
	JMP debut_dump
fin_dump:
	RET