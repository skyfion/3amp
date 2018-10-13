
model thr 
F0 43 7d 60 44 54 41 xx F7
THR5	0x30   = 48
THR10	0x31
THR10X	0x32
THR10C	0x33
THR5A	0x34

SETTINGS REQUEST
                 F0 43 7D 20 44 54 41 31 41 6C 6C 50 F7
                 
                 response example 
                 -16, 67, 125, 0, 2, 12, 68, 84, 65, 49, 65, 108, 108, 80, 0, 0, 127, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 48, 34, 31, 74, 65, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, 1, 14, 48, 36, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 70, 10, 31, 32, 0, 21, 19, 0, 0, 0, 0, 0, 0, 0, 2, 0, 16, 0, 70, 0, 95, 21, 112, 8, 13, 61, 0, 0, 0, 0, 0, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, -9

https://github.com/harjot-oberai/Croller
http://mathis-wip.blogspot.com/

SysEx Format is: F0 43 7D 10 41 30 01 xx xx xx F7


Category	Setting	Value	Range
Amps	
    Clean	00 00 00
	Crunch	00 00 01
	Lead	00 00 02
	BritHi	00 00 03
	Modern	00 00 04
	Bass	00 00 05
	Aco	    00 00 06
	Flat	00 00 07

Cabs	US4x12	06 00 00
	US2x12	06 00 01
	Brit4x12	06 00 02
	Brit2x12	06 00 03
	1x12	06 00 04
	4x10	06 00 05

Controls	
    Gain	01 00 xx	00-64
	Master	02 00 xx	00-64
	Bass	03 00 xx	00-64
	Middle	04 00 xx	00-64
	Treble	05 00 xx	00-64

Gate	On	5f 00 00
	Off	5f 00 7f
	Threshold	51 00 xx	00-64
	Release	52 00 xx	00-64

Compressor	
    On	1f 00 00
	Off	1f 00 7f

	Stomp	10 00 00
	Sustain	11 00 xx	00-64
	Output	12 00 xx	00-64

	Rack	10 00 01
	Threshold	11 xx xx	0000-0458	-60dB : 0
	Attack	13 00 xx	00-64
	Release	14 00 xx	00-64
	Ratio	15 00 xx	00-05	1:1, 1:4, 1:8, 1:12, 1:20, 1:inf
	Knee	16 00 xx	00-02	soft, medium, hard
	Output	17 xx xx	0000-0458	-20dB : 40dB

Modulation
	On	2f 00 00
	Off	2f 00 7f

	Chorus	20 00 00
	Speed	21 00 xx	00-64
	Depth	22 00 xx	00-64
	Mix	    23 00 xx	00-64

	Flanger	20 00 01
	Speed 	21 00 xx	00-64
	Manual	22 00 xx	00-64
	Depth	23 00 xx	00-64
	Feedback	24 00 xx	00-64
	Spread	25 00 xx	00-64

	Tremolo	20 00 02
	Freq	21 00 xx	00-64
	Depth	22 00 xx	00-64

	Phaser	20 00 03
	Speed 	21 00 xx	00-64
	Manual	22 00 xx	00-64
	Depth	23 00 xx	00-64
	Feedback	24 00 xx	00-64
Delay
	On	3f 00 00
	Off	3f 00 7f
	Time	31 xx xx	0001-4e0f  or 1 - 19983
	Feedback	33 00 xx	00-64
	High Cut	34 xx xx	0768-7d01  1896 32001
	Low Cut 	36 xx xx	0015-3e40
	Level	    38 00 xx



Reverb
	On	4f 00 00
	Off	4f 00 7f

	Room	40 00 01
	Plate	40 00 02
	Hall	40 00 00
	Time	41 xx xx	0003-0148
	Pre	43 xx xx	0001-0f50
	LowCUt	45 xx xx	0015-3e40
	HighCut	47 xx xx	0768-7d01
	High ratio	49 00 xx	01-0a
	Low Ratio	4a 00 xx	01-0e
	Level	4b 00 xx	00-64

	Spring	40 00 03
	Reverb	41 00 xx	0-64
	Filter	42 00 xx	0-64

lamp on
F0 43 7D 30 41 30 01 00 F7
lamp off
F0 43 7D 30 41 30 01 01 F7

wide on
F0 43 7D 30 41 30 00 01 F7
wide off
F0 43 7D 30 41 30 00 00 F7
