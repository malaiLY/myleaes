import type { RouteRecordRaw } from "vue-router";

const routes: Array<RouteRecordRaw> = [
  {
    name: "RoomDetail",
    path: "/roomDetail",
    component: () => import("@/views/roomDetail/roomDetail.vue"),
    meta: {
      title: "\u623f\u95f4\u8be6\u60c5",
      noCache: true
    }
  },
  {
    name: "ApartmentDetail",
    path: "/apartmentDetail",
    component: () => import("@/views/apartmentDetail/apartmentDetail.vue"),
    meta: {
      title: "\u516c\u5bd3\u8be6\u60c5",
      noCache: true
    }
  },
  {
    name: "Appointment",
    path: "/appointment",
    component: () => import("@/views/appointment/appointment.vue"),
    meta: {
      title: "\u9884\u7ea6\u770b\u623f",
      noCache: true
    }
  },
  {
    name: "MyAppointment",
    path: "/myAppointment",
    component: () => import("@/views/myAppointment/myAppointment.vue"),
    meta: {
      title: "\u6211\u7684\u9884\u7ea6",
      noCache: true
    }
  },
  {
    name: "MyAgreement",
    path: "/myAgreement",
    component: () => import("@/views/myAgreement/myAgreement.vue"),
    meta: {
      title: "\u6211\u7684\u79df\u7ea6",
      noCache: true
    }
  },
  {
    name: "Agreement",
    path: "/agreement",
    component: () => import("@/views/agreement/agreement.vue"),
    meta: {
      title: "\u79df\u7ea6\u8be6\u60c5",
      noCache: true
    }
  },
  {
    name: "BrowsingHistory",
    path: "/browsingHistory",
    component: () => import("@/views/browsingHistory/browsingHistory.vue"),
    meta: {
      title: "\u6d4f\u89c8\u5386\u53f2",
      noCache: true
    }
  },
  {
    name: "Login",
    path: "/login",
    component: () => import("@/views/login/login.vue"),
    meta: {
      title: "\u767b\u5f55",
      noCache: true
    }
  }
];

export default routes;
