import { createRouter, createWebHistory } from 'vue-router';
import HeartsComponent from './components/HeartsComponent.vue';
import SpadesComponent from './components/games/SpadesComponent.vue';
import JoinLobby from './components/JoinLobby.vue';
import LobbyComponent from './components/LobbyComponent.vue';
import CreateLobbyForm from './components/forms/CreateLobbyForm.vue';
import MainMenu from './components/MainMenu.vue';
import BugReport from './components/index/BugReport.vue';

const routes = [
  { path: '/', component: MainMenu },
  { path: '/hearts', component: HeartsComponent },
  { path: '/spades', component: SpadesComponent },

  { path: '/joinLobby', component: JoinLobby },
  { path: '/createLobby', component: CreateLobbyForm },
  { path: '/lobby', component: LobbyComponent },
  { path: '/bugReport', component: BugReport}
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
