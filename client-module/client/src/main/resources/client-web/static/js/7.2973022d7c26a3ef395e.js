webpackJsonp([7],{idfq:function(e,s){},nuhG:function(e,s){},vyzm:function(e,s,t){"use strict";Object.defineProperty(s,"__esModule",{value:!0});var a={data:function(){return{showData:!1,accountAddressValue:""}},computed:{getAddressList:function(){return this.$store.getters.getAddressList}},created:function(){""!==localStorage.getItem("newAccountAddress")?this.accountAddressValue=localStorage.getItem("newAccountAddress"):this.accountAddressValue=this.$t("message.addressNull"),0===this.$store.getters.getAddressList.length&&this.getAccountList("/account")},methods:{showDataList:function(){0!==this.$store.getters.getAddressList.length?this.showData=!this.showData:"1"!==sessionStorage.getItem("userList")?this.$message({type:"info",message:this.$t("message.c131"),duration:"800"}):this.accountAddressValue=this.$t("message.c134")},getAccountList:function(e){var s=this;this.$fetch(e).then(function(e){console.log(e),e.success?(localStorage.setItem("newAccountAddress",e.data[0].address),localStorage.setItem("encrypted",e.data.list[0].encrypted),s.$store.commit("setAddressList",e.data.list)):s.$store.commit("setAddressList","")}).catch(function(e){console.log("User List err"+e),s.$store.commit("setAddressList","")})},accountAddressChecked:function(e,s){this.showData=!1,this.accountAddressValue=e,this.$emit("chenckAccountAddress",e),localStorage.setItem("newAccountAddress",e),localStorage.setItem("encrypted",s)}}},i={render:function(){var e=this,s=e.$createElement,t=e._self._c||s;return t("div",{staticClass:"address-select1",on:{click:e.showDataList}},[t("div",{staticClass:"sub-selected-value"},[e._v("\n        "+e._s(e.accountAddressValue)+"\n        "),e.showData?t("div",{staticClass:"sub-select-list"},e._l(e.getAddressList,function(s){return t("div",{staticClass:"sub-select-item",on:{click:function(t){t.stopPropagation(),e.accountAddressChecked(s.address,s.encrypted)}}},[e._v("\n                "+e._s(s.address)+"\n            ")])})):e._e()]),e._v(" "),t("i",{staticClass:"el-icon-arrow-up",class:e.showData?"i_reverse":""})])},staticRenderFns:[]};var c={data:function(){return{value0:!1,value1:!1,value2:!0,value3:!1,value4:!0,value5:!1,options:[{value:"zh",label:this.$t("message.c83")},{value:"en",label:this.$t("message.c84")}],outerVisible:!1,type:0,tips:"",downloadPercent:0,encrypted:!1}},components:{SwitchAddressBar:t("VU/8")(a,i,!1,function(e){t("idfq")},null,null).exports},beforeCreate:function(){},created:function(){"true"===localStorage.getItem("encrypted")?this.encrypted=!0:this.encrypted=!1},destroyed:function(){},methods:{chenckAccountAddress:function(e){localStorage.setItem("newAccountAddress",e)},toBackups:function(){sessionStorage.setItem("isActive",1),this.$router.push({name:"/userInfo"})},toUserAddressList:function(){this.$router.push({path:"/users/userAddressList"})},toEditPassword:function(){this.$store.getters.getNetWorkInfo.localBestHeight===this.$store.getters.getNetWorkInfo.netBestHeight?this.encrypted?this.$router.push({name:"/editorPassword",params:{address:localStorage.getItem("newAccountAddress"),backInfo:this.$t("message.setManagement")}}):this.$router.push({name:"/setPassword",params:{address:localStorage.getItem("newAccountAddress"),backInfo:this.$t("message.setManagement")}}):this.$message({message:this.$t("message.c133"),duration:"800"})},versionUpdates:function(){var e=this;this.outerVisible=!0,setTimeout(function(){3!==e.type&&(e.outerVisible=!1)},3e3)}}},o={render:function(){var e=this,s=e.$createElement,t=e._self._c||s;return t("div",{staticClass:"set-page"},[t("h2",[e._v(e._s(e.$t("message.c66")))]),e._v(" "),t("div",{staticClass:"set-page-info"},[t("el-switch",{directives:[{name:"show",rawName:"v-show",value:!1,expression:"false"}],attrs:{width:30,"active-color":"#658ec7","inactive-color":"#0b1422","inactive-text":e.$t("message.c67")},model:{value:e.value0,callback:function(s){e.value0=s},expression:"value0"}}),e._v(" "),t("el-collapse",{directives:[{name:"show",rawName:"v-show",value:!1,expression:"false"}]},[t("el-collapse-item",[t("template",{slot:"title"},[t("el-switch",{attrs:{width:30,"active-color":"#658ec7","inactive-color":"#0b1422","inactive-text":e.$t("message.c68")},model:{value:e.value1,callback:function(s){e.value1=s},expression:"value1"}})],1),e._v(" "),t("ul",[t("li",[t("el-switch",{attrs:{width:30,"active-color":"#658ec7","inactive-color":"#0b1422","inactive-text":e.$t("message.c69")},model:{value:e.value2,callback:function(s){e.value2=s},expression:"value2"}})],1),e._v(" "),t("li",[t("el-switch",{attrs:{width:30,"active-color":"#658ec7","inactive-color":"#0b1422","inactive-text":e.$t("message.c70")},model:{value:e.value3,callback:function(s){e.value3=s},expression:"value3"}})],1),e._v(" "),t("li",[t("el-switch",{attrs:{width:30,"active-color":"#658ec7","inactive-color":"#0b1422","inactive-text":e.$t("message.c71")},model:{value:e.value4,callback:function(s){e.value4=s},expression:"value4"}})],1),e._v(" "),t("li",[t("el-switch",{attrs:{width:30,"active-color":"#658ec7","inactive-color":"#0b1422","inactive-text":e.$t("message.c72")},model:{value:e.value5,callback:function(s){e.value5=s},expression:"value5"}})],1)])],2)],1),e._v(" "),t("div",{directives:[{name:"show",rawName:"v-show",value:!1,expression:"false"}],staticClass:"set-page-div"},[t("label",[e._v("切换账户：")]),e._v(" "),t("SwitchAddressBar",{on:{chenckAccountAddress:e.chenckAccountAddress}})],1),e._v(" "),t("div",{staticClass:"set-page-div"},[t("label",[e._v(e._s(e.$t("message.c73"))+"：")]),e._v(" "),t("span",{staticClass:"cursor-p set-page-div-span",on:{click:e.toBackups}},[e._v(e._s(e.$t("message.c74")))])]),e._v(" "),t("div",{staticClass:"set-page-div"},[t("label",[e._v(e._s(e.$t("message.c77"))+"：")]),e._v(" "),t("span",{staticClass:"cursor-p set-page-div-span",on:{click:e.toUserAddressList}},[e._v(e._s(e.$t("message.c78")))])]),e._v(" "),t("div",{staticClass:"set-page-div"},[t("label",[e._v(e._s(e.$t("message.c79"))+"：")]),e._v(" "),t("span",{staticClass:"cursor-p set-page-div-span",on:{click:e.toEditPassword}},[e._v(" "+e._s(this.encrypted?e.$t("message.c160"):e.$t("message.c161")))])]),e._v(" "),t("div",{staticClass:"set-page-div"},[t("label",[e._v(e._s(e.$t("message.c81"))+"：V"+e._s(this.$store.getters.getPurseVersiont))]),e._v(" "),t("span",{staticClass:"cursor-p set-page-div-span",on:{click:e.versionUpdates}},[e._v(e._s(e.$t("message.c82")))])])],1),e._v(" "),t("el-dialog",{attrs:{title:e.$t("message.c151"),visible:e.outerVisible,"close-on-click-modal":!1,"close-on-press-escape":!1,"custom-class":"version-dialog","show-close":3!==this.type,top:"30vh",center:!0},on:{"update:visible":function(s){e.outerVisible=s}}},[t("div",{staticClass:"progress-info"},[t("h2",[e._v(e._s(this.tips))]),e._v(" "),t("div",{directives:[{name:"show",rawName:"v-show",value:3===this.type,expression:"this.type === 3 "}],staticClass:"progress"},[t("el-progress",{directives:[{name:"show",rawName:"v-show",value:!1,expression:"false"}],attrs:{percentage:this.downloadPercent}}),e._v(" "),t("p",[e._v(e._s(e.$t("message.c152")))])],1)]),e._v(" "),t("div",{directives:[{name:"show",rawName:"v-show",value:3===this.type,expression:"this.type === 3 "}],staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[t("el-button",{attrs:{type:"primary"},on:{click:function(s){e.outerVisible=!1}}},[e._v(e._s(e.$t("message.c153")))])],1)])],1)},staticRenderFns:[]};var n=t("VU/8")(c,o,!1,function(e){t("nuhG")},null,null);s.default=n.exports}});
//# sourceMappingURL=7.2973022d7c26a3ef395e.js.map