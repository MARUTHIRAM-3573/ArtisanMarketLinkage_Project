import { registerRootComponent } from "expo";

import App from "./App";

// registerRootComponent calls AppRegistry.registerComponent('main', () => App),
// and sets up the environment appropriately for Expo's managed workflow,
// whether the app is loaded via Expo Go or a native build.
registerRootComponent(App);
