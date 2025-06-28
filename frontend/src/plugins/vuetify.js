import 'vuetify/styles'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import "vuetify/styles";

import {fa} from "vuetify/iconsets/fa";
import {aliases, mdi} from "vuetify/lib/iconsets/mdi";
import "@mdi/font/css/materialdesignicons.css";
import {createVuetify} from "vuetify";

const vuetify = createVuetify({
    icons: {
        defaultSet: "mdi",
        aliases,
        sets: {
            mdi,
            fa,
        },
    },
    components,
    directives,
});

export default vuetify
