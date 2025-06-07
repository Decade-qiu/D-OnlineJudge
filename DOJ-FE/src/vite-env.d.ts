/// <reference types="vite/client" />

// 识别.vue文件
declare module "*.vue" {
    import { DefineComponent } from "vue";
    const component:DefineComponent<{},{},any>
    export default component
 
}

// 定义import.meta.env类型
interface ImportMetaEnv{
    readonly VITE_APP_URL:string,
    readonly VITE_APP_TITLE:string,
    readonly NODE_ENV:string,
}


// 自定义组件类型
import Editor from '@/components/CodeEditor/index.vue';
declare module 'vue' {
  export interface GlobalComponents {
    Editor: typeof Editor
  }
}