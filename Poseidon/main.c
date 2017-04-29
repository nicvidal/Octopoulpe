#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

#include "server/parser.h"
#include "server/server.h"
#include "model/aquarium.h"
#include "utility/tools.h"
#include "view/view.h"
#include "utility/data.h"
#include "server/world.h"

#include "server/answer.h"

extern struct _tvector* config_vector;
struct aquarium* aquarium;

int main(int argc, char* argv[]){

    pthread_t menu_t;
    pthread_t server_t;
    pthread_t world_t;

    // Initialisation of the aquarium
    aquarium = malloc(sizeof(struct aquarium));
    aq__initialize_aquarium(aquarium, AQUARIUM_DIMENSIONS);
    
    char* view1 = aq__add_view(aquarium, (struct position) {0, 0}, AQUARIUM_DIMENSIONS, "N1");
    //char* view2 = aq__add_view(aquarium, (struct position) {100, 100}, (struct dimension) {900, 900}, "N2");

    //Job to do with config file ... (before launching server)
    parse_config_file("controller.cfg");
    int port = 8000;

    SPEED_RATE = _get_value(config_vector, "fish-update-interval");

    CHK_ERROR(pthread_create(&world_t, NULL, world_process , NULL), "world thread");
    CHK_ERROR(pthread_create(&menu_t, NULL, main_menu, NULL), "main_menu thread")
    CHK_ERROR(pthread_create(&server_t, NULL, server_process, &port), "server thread");

    printf("Display timeout value : %d\n", _get_value(config_vector, "fish-update-interval"));
    _delete_tvector(config_vector);

    _set_verbosity(FALSE);
    _console_log(LOG_HIGH, "BIG PROBLEM");
    _console_log(LOG_LOW, "simple log");

    struct dimension def = (struct dimension){1, 1};

    aq__add_fish_to_aqv(aquarium, view1, fish__create(BLOBFISH, 10, 20, "jeanmi", HORIZONTAL, def, SPEED_RATE));
    aq__set_fish_running_state(aquarium, "jeanmi", 1);
    //aq__add_fish_to_aqv(aquarium, view1, fish__create(BLOBFISH, 10, 30, "jeanma", HORIZONTAL, def));
    //aq__add_fish_to_aqv(aquarium, view2, fish__create(OCTOPUS, 300, 300, "jeanmo", HORIZONTAL, def));
    //aq__add_fish_to_aqv(aquarium, view2, fish__create(OCTOPUS, 400, 400, "jeanbite", HORIZONTAL, def));

    //aq__remove_fish(aquarium, "jeanbite");

    display_aquarium(aquarium);

    //aq__remove_fish(aquarium, "jeanmi");
    //aq__remove_fish(aquarium, "jeanma");
    display_aquarium(aquarium);

    pthread_join(menu_t, NULL);
    pthread_join(server_t, NULL);
    pthread_join(world_t, NULL);

    return EXIT_SUCCESS;

    // To test : a false aquarium
    /*    aquarium = malloc(sizeof(struct aquarium));
        aq__initialize_aquarium(aquarium, AQUARIUM_DIMENSIONS);
        aq__add_view(aquarium, (struct position) {250, 250}, (struct dimension) {500, 500}, "Cookie");
        aq__add_view(aquarium, (struct position) {100, 100}, (struct dimension) {900, 900}, "Donald");
        // end of the test

        char * res = malloc(sizeof(400));
        struct client *henry = malloc(sizeof(struct client));
        henry->id = NULL;
        asw__hello("\n", res, henry);
        printf("main\t%s", res);
        asw__hello("\n", res, henry);
        printf("main\t%s", res);
        asw__hello("\n", res, henry);
        printf("main\t%s", res);

        // Henry a la vue "Cookie"
        struct dimension def = (struct dimension){1, 1};
        aq__add_fish_to_aqv(aquarium,"Cookie",fish__create(BLOBFISH, 10, 20, "Bibi", HANDV, def));
        aq__add_fish_to_aqv(aquarium,"Cookie",fish__create(BLOBFISH, 40, 50, "Bobo",HANDV,def));

        asw__get_fishes("\n",res,henry);
        printf("main\t%s",res);

        asw__log("out\n",res,henry);
        printf("main\t%s",res);
        return EXIT_SUCCESS;*/
}
