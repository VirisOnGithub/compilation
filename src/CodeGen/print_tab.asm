
'[' : 91
']' : 93
' ' : 32

print_tab: // *print_tab
	SUBi SP SP 1
	LD R1 SP		# profondeur
	SUBi SP SP 1
	LD R2 SP		# pointeur du tableau
	LD R3 R2		# longueur du tableau
	XOR R4 R4 R4
	ADDi R4 R4 91
	OUT R4			# on affiche "["
	SUBi R4 R4 59
	OUT R4			# on affiche " "
	XOR R5 R5 R5	# i = 0
	ADDi R2 R2 1	# on pointe maintenant sur le premier élément
debut_boucle_print_tab: // *loop_start
	JEQU R5 R3 fin_pour_print_tab
	XOR R6 R6 R6
	MODi R7 R5 10
	JNEQ R7 R6 print_element	# i % 10 != 0
	JEQU R5 R6 print_element	# i == 0
	LD R2 R2
	XOR R9 R9 R9
	ADDi R9 R9 1
	JEQU R1 R9 print_element
	ST R3 SP						# on empile la longueur du tableau pour la retrouver plus tard
	ADDi SP SP 1
	ST R2 SP						# on empile tab
	ADDi SP SP 1
	SUBi R11 R1 1					# R11 := profondeur - 1
	ST R11 SP						# on empile profondeur - 1
	ADDi SP SP 1
	CALL print_tab
	// revenir au bon état
	ADDi SP SP 1
	LD R1 SP						# on dépile profondeur - 1
	ADDi R1 R1 1					# profondeur++
	SUBi SP SP 1
	LD R2 SP						# on dépile le pointeur du tableau
	SUBi SP SP 1
	LD R3 SP						# on dépile la taille du tableau
	JMP fin_print_element
print_element: // *print_elem
	PRINT R2
fin_print_element: // *print_elem_end
	XOR R8 R8 R8
	ADDi R8 R8 32
	OUT R8			# on affiche " "
	ADDi R2 R2 1	# on pointe maintenant sur le prochain élément
	JMP debut_boucle_print_tab
fin_pour_print_tab: // *print_end
	XOR R4 R4 R4
	ADDi R4 R4 93
	OUT R4 			# on affiche "]"
	RET
