webpackJsonp([20],{SwHk:function(e,s,t){"use strict";Object.defineProperty(s,"__esModule",{value:!0});var o=t("LPk9"),n=t("+1pJ"),a=t("FJop"),r=(t("QmSG"),t("x47x")),c=(t("9woI"),{data:function(){var e=this;return{submitId:"newNode",accountAddress:[],copyValue:localStorage.getItem("newAccountAddress"),usable:"0",fee:0,placeholder:"",newNodeForm:{accountAddressValue:localStorage.getItem("newAccountAddress"),packingAddress:"",deposit:"",commissionRate:""},newNodeRules:{packingAddress:[{validator:function(s,t,o){t?/[A-Za-z].*[0-9]|[0-9].*[A-Za-z]/.exec(t)?t===localStorage.getItem("newAccountAddress")?o(new Error(e.$t("message.c169"))):o():o(new Error(e.$t("message.c168"))):o(new Error(e.$t("message.c38")))},trigger:"blur"}],deposit:[{validator:function(s,t,o){t||o(new Error(e.$t("message.c31"))),setTimeout(function(){if(/(^\+?|^\d?)\d*\.?\d+$/.exec(t)&&/^\d{1,8}(\.\d{1,8})?$/.exec(t)){var s=new r.BigNumber(1e8);parseInt(s.times(t).toString())>parseInt(s.times(e.usable).toString())?o(new Error(e.$t("message.c543"))):parseInt(s.times(t).toString())<parseInt(s.times(2e4).toString())?o(new Error(e.$t("message.c541"))):o()}else o(new Error(e.$t("message.c136")))},100)},trigger:"blur"}],commissionRate:[{validator:function(s,t,o){t||o(new Error(e.$t("message.c37")));/^\d+(?=\.{0,1}\d+$|$)/.exec(t)&&/^\d{1,3}(\.\d{1,2})?$/.exec(t)?10>t||t>100?o(new Error(e.$t("message.c37"))):o():o(new Error(e.$t("message.c36")))},trigger:"blur"}]}}},components:{Back:o.a,AccountAddressBar:n.a,Password:a.a},mounted:function(){this.getBalanceAddress("/account/balance/"+localStorage.getItem("newAccountAddress"))},methods:{chenckAccountAddress:function(e){var s=this;this.newNodeForm.accountAddressValue=e,this.copyValue=e,this.getBalanceAddress("/account/balance/"+e),this.countFee(),setTimeout(function(){""!==s.newNodeForm.deposit&&s.$refs.newNodeForm.validateField("deposit")},500)},accountCopy:function(){javaUtil.copy(this.copyValue),this.$message({message:this.$t("message.c129"),type:"success",duration:"800"})},getBalanceAddress:function(e){var s=this;this.$fetch(e).then(function(e){e.success&&(s.usable=(1e-8*e.data.usable).toFixed(8),s.placeholder=s.$t("message.currentBalance")+" "+(1e-8*e.data.usable).toFixed(8))})},countFee:function(){var e=this;if(""!==this.newNodeForm.packingAddress&&""!==this.newNodeForm.commissionRate&&this.newNodeForm.deposit>0){var s=new r.BigNumber(1e8),t="agentAddress="+this.newNodeForm.accountAddressValue+"&packingAddress="+this.newNodeForm.packingAddress+"&rewardAddress="+this.newNodeForm.accountAddressValue+"&commissionRate="+this.newNodeForm.commissionRate+"&deposit="+s.times(this.newNodeForm.deposit);this.$fetch("/consensus/agent/fee?"+t).then(function(s){if(s.success){var t=new r.BigNumber(1e-8);e.fee=t.times(s.data.value)}})}},submitForm:function(e){var s=this;this.$store.getters.getNetWorkInfo.localBestHeight===this.$store.getters.getNetWorkInfo.netBestHeight&&"true"===sessionStorage.getItem("setNodeNumberOk")?this.$refs[e].validate(function(e){if(!e)return!1;"true"===localStorage.getItem("encrypted")?s.$refs.password.showPassword(!0):s.$confirm(s.$t("message.c166"),"",{confirmButtonText:s.$t("message.confirmButtonText"),cancelButtonText:s.$t("message.cancelButtonText")}).then(function(){s.toSubmit("")}).catch(function(){})}):this.$message({message:this.$t("message.c133"),duration:"800"})},toSubmit:function(e){var s=this,t=new r.BigNumber(1e8),o='{"agentAddress":"'+this.newNodeForm.accountAddressValue+'","packingAddress":"'+this.newNodeForm.packingAddress+'","commissionRate":"'+this.newNodeForm.commissionRate+'","deposit":"'+parseFloat(t.times(this.newNodeForm.deposit).toString())+'","password":"'+e+'"}';this.$post("/consensus/agent ",o).then(function(e){e.success?(s.$message({type:"success",message:s.$t("message.passWordSuccess")}),s.$router.push({name:"/consensus",params:{activeName:"first"}})):s.$message({type:"warning",message:s.$t("message.passWordFailed")+e.msg})})}}}),i={render:function(){var e=this,s=e.$createElement,t=e._self._c||s;return t("div",{staticClass:"new-node"},[t("Back",{attrs:{backTitle:this.$t("message.consensusManagement")}}),e._v(" "),t("h2",[e._v(e._s(e.$t("message.c21")))]),e._v(" "),t("div",{staticClass:"new-node-form"},[t("el-form",{ref:"newNodeForm",attrs:{model:e.newNodeForm,rules:e.newNodeRules,size:"mini","label-position":"top"}},[t("el-form-item",{staticClass:"a-new",attrs:{label:e.$t("message.c22")+":"}},[t("AccountAddressBar",{on:{chenckAccountAddress:e.chenckAccountAddress}}),e._v(" "),t("i",{staticClass:"copy_icon copyBtn cursor-p",attrs:{"data-clipboard-text":e.copyValue,title:e.$t("message.c143")},on:{click:e.accountCopy}})],1),e._v(" "),t("el-form-item",{attrs:{label:e.$t("message.c23")+":",prop:"packingAddress"}},[t("el-input",{attrs:{placeholder:this.$t("message.c167"),maxlength:35},on:{change:e.countFee},model:{value:e.newNodeForm.packingAddress,callback:function(s){e.$set(e.newNodeForm,"packingAddress","string"==typeof s?s.trim():s)},expression:"newNodeForm.packingAddress"}})],1),e._v(" "),t("el-form-item",{attrs:{label:e.$t("message.c25")+"（NULS）:",prop:"deposit"}},[t("el-input",{attrs:{placeholder:this.placeholder,maxlength:17},on:{change:e.countFee},model:{value:e.newNodeForm.deposit,callback:function(s){e.$set(e.newNodeForm,"deposit",s)},expression:"newNodeForm.deposit"}})],1),e._v(" "),t("el-form-item",{attrs:{label:e.$t("message.c26")+"（%）:",prop:"commissionRate"}},[t("el-input",{attrs:{maxlength:5},on:{change:e.countFee},model:{value:e.newNodeForm.commissionRate,callback:function(s){e.$set(e.newNodeForm,"commissionRate",s)},expression:"newNodeForm.commissionRate"}})],1),e._v(" "),t("el-form-item",{attrs:{label:e.$t("message.c28")+": "+this.fee+" NULS"}}),e._v(" "),t("el-form-item",{staticClass:"submit",attrs:{size:"large"}},[t("el-button",{attrs:{type:"primary",id:"newNode"},on:{click:function(s){e.submitForm("newNodeForm")}}},[e._v("\n          "+e._s(e.$t("message.confirmButtonText"))+"\n        ")])],1)],1)],1),e._v(" "),t("Password",{ref:"password",attrs:{submitId:e.submitId},on:{toSubmit:e.toSubmit}})],1)},staticRenderFns:[]};var d=t("VU/8")(c,i,!1,function(e){t("l6m/")},null,null);s.default=d.exports},"l6m/":function(e,s){}});
//# sourceMappingURL=20.9437d92fe33e6492c03a.js.map