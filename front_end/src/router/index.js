import { createRouter, createWebHistory } from "vue-router"

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: "/",
            redirect: "/Home",
        },
        {
            path: "/Home",
            name: "home",
            component: () => import('@/views/mainPage/HomeView.vue'),
        },
        {
            path: "/Welcome",
            name: "welcome",
            component: () => import('@/components/WelcomeView.vue'),
            children : [
                {
                    // 子路由的 path 如果以 / 开头，就会被当成根路径，不再拼接父路由的路径
                    // <router-view/> 一旦没有匹配到任何子路由，父路由页面就会变成空白的

                    // 登录子页面
                    path: "Login",
                    name: "home-SignIn",
                    component: () => import('@/views/welcomePage/LogInView.vue')
                },
                {
                    // 注册子页面
                    path: "Signup",
                    name: "home-SignUp",
                    component: () => import('@/views/welcomePage/SignUpView.vue')
                }
            ]
        },
        {
            path: "/Events",
            name: "events",
            component: () => import('@/views/eventsPage/eventsView.vue'),
            children: []
        },
        {
            path: "/Dashboard",
            name: "dashboard",
            component: () => import('@/views/dashboardPage/dashBoardView.vue'),
            children: []
        },
        {
            path: "/Manage",
            name: "manage",
            component: () => import('@/views/managePage/manageView.vue'),
            children: []
        },
        {
            path: "/Events/:eventName",
            name: "eventDetail",
            component: () => import('@/views/eventsPage/eventDetailView.vue'),
            children: []
        },
        {
            path: "/:sub_eventName",
            name: "sub_eventDetail",
            component: () => import('@/views/eventsPage/sub_eventDetailView.vue'),
            children: []
        }
    ],
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition;
        }
        if (to.hash) {
            return {el: to.hash, behavior: 'smooth'};
        }
        return {left: 0, top: 0};
    }
})

router.beforeEach((to, from, next) => {
    const formattedPath = to.path.replace(/^\/\w/, (c) => c.toUpperCase());
    if (to.path !== formattedPath) {
        next({ path: formattedPath, query: to.query, replace: true });
    } else {
        next();
    }
});

export default router