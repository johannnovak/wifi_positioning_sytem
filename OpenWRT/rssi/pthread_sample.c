#include <pthread.h>

/* Content omitted */

static void * my_func ( void * args )
{
  int max = (int) args;
  int i;
  int * ret = (int*)malloc ( sizeof ( int ) );

  /* If sharing memory with main process,
     use inter-process synchronization */
  for ( i = 0 ; i < max ; ++i ) {
    (*ret) += i;
  }
  pthread_exit ( (void*)ret );
}

/* Content omitted */

/*
pthread_t comm_thread;
struct thread_params tp;
int * my_ret;
*/

/* Content omitted */

// pthread_create ( &my_thread, NULL, my_func, (void *) &tp );

/* Content omitted */

// pthread_join ( my_thread, void (**) &my_ret );

/* Content omitted */
