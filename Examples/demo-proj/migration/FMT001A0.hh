#include <assem.h>

/* EQUates table */



/* Files */



static struct {
/*           --> CSECT: CSECT001 <-- */
  BYTE  __FILLER_csect001_0001[104];
  BYTE  __wdub[8];
  FWORD __wsave[18];
/*COUNT*/
  DECIMAL(4, __wct);
/*NO OF CREDIT TRANS*/
  DECIMAL(4, __wnum);
/*AMOUNT*/
  DECIMAL(4, __wtotal);

/* CSECT #defines */
#define FILLER_csect001_0001 local.__FILLER_csect001_0001
#define wdub local.__wdub
#define wsave local.__wsave
#define wct local.__wct
#define wnum local.__wnum
#define wtotal local.__wtotal
} __attribute__((packed)) local;


/*           --> EXPORTED DATA <-- */
int fmt001a0(regs_t * p_regs);
FWORD V_FMT001A0 = (FWORD)&fmt001a0;

