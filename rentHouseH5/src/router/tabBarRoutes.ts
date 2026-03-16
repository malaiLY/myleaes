import type { RouteRecordRaw } from "vue-router";

const routes: Array<RouteRecordRaw> = [
  {
    name: "Search",
    path: "/search",
    component: () => import("@/views/search/search.vue"),
    meta: {
      title: "\u627e\u623f",
      icon: "search"
    }
  },
  {
    path: "/userCenter",
    name: "UserCenter",
    component: () => import("@/views/userCenter/userCenter.vue"),
    meta: {
      title: "\u4e2a\u4eba\u4e2d\u5fc3",
      icon: "user-o",
      isShowNavBar: true
    }
  }
];

export default routes;
