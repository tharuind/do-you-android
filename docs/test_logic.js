/* Minimal browser API stubs for smoke-testing all app views in Node without an Android SDK */
const vm=require('vm'),fs=require('fs'),assert=require('assert');
let html='',events={},saved={},appEl={set innerHTML(v){html=v},get innerHTML(){return html}},body={appendChild(e){},insertAdjacentHTML(where,h){html+='\n'+h}},window={};
const doc={getElementById(id){return id==='app'?appEl:id==='view'?{scrollTo(){}}:null},querySelector(){return null},addEventListener(type,fn){events[type]=fn},documentElement:{style:{}},body,createElement(){return {remove(){},style:{}}}};
const storage={getItem(k){return saved[k]||null},setItem(k,v){saved[k]=v}};
class MockFormData { constructor(form){this.obj=form.fields||{}} entries(){return Object.entries(this.obj)} };
const context={document:doc,window,localStorage:storage,console,Date,Math,Number,String,Array,Object,Boolean,JSON,Intl,Map,Set,parseInt,parseFloat,isNaN,FormData:MockFormData,URL,TextEncoder,Blob,setInterval(){},setTimeout(fn){return 1},clearTimeout(){},navigator:{},alert(){},confirm(){return true}};
vm.createContext(context);
let source=fs.readFileSync(__dirname+'/../app/src/main/assets/app.js','utf8');vm.runInContext(source,context);
assert(html.includes('Make today yours.'));console.log('OK: startup Today');
const pages=['today','inbox','tasks','upcoming','completed','trash','calendar','lists','kanban','matrix','goals','smart','habits','focus','countdown','notes','stats','automation','timeline','settings'];
for(let page of pages){let el={dataset:{act:'nav',page},matches(){return true}},e={target:{closest(){return el}},preventDefault(){}};events.click(e);assert(html.includes('class="app"'),page+' app missing');assert(html.includes('class="view"'),page+' view missing');console.log('OK:',page, 'HTML',html.length)}
// Task editor DOM render and search overlay is validated using synthetic insertion.
for(let act of ['quick-add','add-note','add-goal','add-habit','add-rule','add-list','add-countdown','search']){let el={dataset:{act},matches(){return true}},e={target:{closest(){return el}},preventDefault(){}};events.click(e);assert(html.includes('class="modal"'),act+' did not open modal');}
let form={id:'edit-form',dataset:{kind:'task'},fields:{title:'Write a lab report',notes:'My lab notes',priority:'high',status:'todo',listId:'inbox',dueAt:'2026-11-30T15:30',deadline:'2026-12-01',startAt:'',tags:'science, university',repeat:'weekly',repeatDays:'1',reminder:'10',estimate:'120',subtasks:'Find papers\\nWrite draft'},querySelector(){return null}};
// Previous action is Search; close overlay, create task, then submit.
let quick={dataset:{act:'quick-add'},matches(){return true}};events.click({target:{closest(){return quick}},preventDefault(){}});
events.submit({target:form,preventDefault(){}});
let store=JSON.parse(saved['do-you-data-v1']);assert(store.tasks.some(t=>t.title==='Write a lab report'));console.log('OK: task creation stored in localStorage');
let task=store.tasks.find(t=>t.title==='Write a lab report');let toggle={dataset:{act:'toggle-task',id:task.id},matches(){return true}};events.click({target:{closest(){return toggle}},preventDefault(){}});
store=JSON.parse(saved['do-you-data-v1']);assert(store.tasks.find(t=>t.id===task.id).status==='done');assert(store.tasks.some(t=>t.seriesId===task.id&&t.status==='todo'));console.log('OK: task completed and weekly recurrence generated');
console.log('PASS - 20+ screen / edit forms / persisted task / recurrence smoke test');
