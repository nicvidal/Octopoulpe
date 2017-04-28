#ifndef POSEIDON_ANSWER_H
#define POSEIDON_ANSWER_H

#include <sys/queue.h>
#include <pthread.h>
#include <time.h>

#define HELLO_SUCCESS 0
#define HELLO_FAILURE 1
#define HELLO_INVALID 2

struct client {
    char * id;
    int is_free;
    int _connected;
    struct aquarium_view *aqv;
    LIST_ENTRY(client) entries;

    pthread_t _continuous_sender;
    time_t _send_delay;
    int _socket_fd;
};

/* Functions for the server thread */
/**
 * @brief asw__hello    the handler for the "hello" command from the client
 * @param arg           the end of the command which represents the arguments, should finish with '\n'
 * @param res           the string used to communicate the answer
 *                      "greeting <VIEW IDENTIFIER>\n"  with an available view identifier
 *                      "no greeting\n"                 if none of the view identifier was available
 *                      The command syntax            if the arguments were incorrect
 * @param cli           the client who has made the command, at the end, it would be correctly initialized
 * @return              HELLO_SUCCESS       if a view identifier was attributed to the client
 *                      HELLO_FAILURE       else, a deconnection will be processed by the server thread
 */
int asw__hello(char * arg, char ** res, struct client *cli);

/**
 * @brief asw__get_fishes   the handler for the "hello" command from the client
 * @param arg               the end of the command which represents the arguments, should finish with '\n'
 * @param res               the string used to communicate the answer
 *                          "list [<FISH_NAME> at <POS%_X>x<POS%_Y>,<FISH_WIDTH>x<FISH_HEIGHT>,<SEC>] [<...>]\n"
 *                          FISH_NAME   the name of the fish (an identifier)
 *                          POS%_X      the new horizontal position in percentage of the screen width
 *                          POS%_Y      the new vertical position in percentage of the screen height
 *                          FISH_WIDTH  the width of the picture used to show the fish on the screen
 *                          FISH_HEIGHT the height of the picture used to show the fish on the screen
 *                          SEC         how many seconds has to last the move from the current position to the new one
 *                                      if SEC equals 0, the fish is shown immediately
 */
void asw__get_fishes(char ** res, struct client *cli);
void asw__get_fishes_continuously(char * arg, char * res); // à voir avec Louise : il faut faire une boucle d'attente active !
void asw__ping(char * arg, char * res); // à voir avec Louise car horloge : le traitant remette à zéro une horloge ? Et dans le thread serveur, un thread qui coupe la communication une fois l'horloge arrivée à expiration OU signal handler !!
char* asw__log(char* arg, struct client *cli);
void asw__add_fish(char * arg, char * res, struct client *cli);
void asw__del_fish(char * arg, char * res, struct client *cli);
void asw__start_fish(char * arg, char * res, struct client *cli);

/* Functions for the aquarium */
void asw__init_aquarium(void);
void asw__add_view(struct aquarium_view * view);
void asw__remove_view(char *id);
void asw__remove_aquarium(void);

#endif //POSEIDON_ANSWER_H
