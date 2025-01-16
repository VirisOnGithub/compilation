tab_access:
	SUBi SP SP 1
	LD R0 SP				        # profondeur
	SUBi SP SP 1
	LD R1 SP				        # index
	SUBi SP SP 1
	LD R2 SP				        # pointeur
	LD R3 R2				        # longueur
	ADDi R4 R3 0			        # nouvelle_longueur = longueur
	JIEQ R1 R3 skip_resize	        # if (index > longueur)
	ADDi R4 R1 1			        # nouvelle_longueur = index + 1
skip_resize:
	ST R4 R2				        # *pointeur = nouvelle_longueur
	ADDi R2 R2 1			        # pointeur++
	XOR R5 R5 R5			        # i = 0
	XOR R6 R6 R6			        # constante à 0
begin_loop:
	MODi R7 R5 10					# R7 := i % 10
	JNEQ R7 R6 skip_tab_access_end	# i % 10 != 0
	JEQU R5 R6 skip_tab_access_end	# i == 0
	JIEQ R3 R0 skip_alloc
	ST TP R2						# *pointeur = TP
	ADDi TP TP 11					# TP += 11
skip_alloc:
	LD R2 R2						# pointeur = *pointeur
skip_tab_access_end:
	JINF R5 R3 skip_fill			# if (i >= longueur)
	JNEQ R0 R6 skip_simple_init		# if (profondeur == 0)
	ST R6 R2						# *pointeur = 0
	JMP skip_fill					
skip_simple_init:					# else
	ADDi R8 R2 0					# oldPointeur = pointeur
	ST TP R2						# *pointeur = TP
	ADDi TP TP 12					# TP += 12
	LD R2 R2 						# *pointeur = pointeur
	ST R6 R2						# *pointeur = 0
	ADDi R2 R8 0					# pointeur = oldPointeur
skip_fill:
	JNEQ R5 R1 skip_return			# if (i == index)
	ST R2 SP			            # on empile le pointeur dans le tableau
	ADDi SP SP 1
	RET					            # et on le retourne
skip_return:
	ADDi R2 R2 1		            # pointeur++
	ADDi R5 R5 1		            # i++
	JMP begin_loop