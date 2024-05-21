import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [{
  path: '/',
  name: 'index',
  component: () => import('../dashboard/template/index.vue')
}]
const router = new VueRouter({
  mode: "history",
  base: "datav",
  routes
})

export default router