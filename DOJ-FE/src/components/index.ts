import { App, Component } from 'vue';
import svgIcon from './SvgIcon/index.vue';
import CodeEditor from './CodeEditor/index.vue';

type GlobalComponents = {
    [key: string]: Component
}
const allGlobalComponents: GlobalComponents = {svgIcon, CodeEditor};
// 对外暴露插件对象
export default {
    install(app: App){
        Object.keys(allGlobalComponents).forEach(key => {
            app.component(key, allGlobalComponents[key]);
        });
    }
}