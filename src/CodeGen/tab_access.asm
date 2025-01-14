tab_access:
	XOR R6 R6 R6	# constante à 0
	SUBi SP SP 1
	LD R0 SP		# index
	SUBi SP SP 1
	LD R1 SP		# pointeur du tableau
	LD R2 R1		# longueur du tableau
	XOR R3 R3 R3	# i = 0
	JIEQ R0 R2 skip_resize
	ADDi R2 R0 1	# longueur = index + 1
	ST R1 R2		# on met à jour la longueur
skip_resize:
	ADDi R1 R1 1	# pointeur++
begin_loop:
	MODi R5 R3 10
	JNEQ R5 R6 skip_tab_access_end	# i % 10 != 0
	JEQU R3 R6 skip_tab_access_end	# i == 0
	JIEQ R3 R2 skip_alloc
	ST TP R1			# *pointeur = TP
	ADDi TP TP 11		# TP += 11
skip_alloc:
	LD R1 R1			# pointeur = *pointeur
skip_tab_access_end:
	JIEQ R3 R2 skip_fill
	ST R6 R1
skip_fill:
	JNEQ R3 R0 skip_return
	LD R1 R1 			# pointeur = *pointeur
	ST R1 SP			# on empile la valeur dans le tableau
	ADDi SP SP 1
	RET					# et on retourne
skip_return:
	ADDi R1 R1 1		# pointeur++
	ADDi R3 R3 R3		# i++
	JMP begin_loop