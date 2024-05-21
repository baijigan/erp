import store from "@/store"
//处理部门数据字段children为0时设为undefined，用于弹框选择部门回显bug
export function getTreeData(data) {
  for (let i = 0; i < data.length; i++) {
    if (data[i].children.length < 1) {
      // children若为空数组，则将children设为undefined
      data[i].children = undefined;
    } else {
      // children若不为空数组，则递归调用 本方法
      getTreeData(data[i].children);
    }
  }
  return data;
}
/**
 * 根据部门id获取级联选择框回显数据
 * data 树形数据
 * id 接收的部门id
 * parentArr 父部门信息
 * childrenArr 子部门信息
 */
export function getEcho(id,data,parentArr,childrenArr) {
 return new Promise((resolve,reject)=>{
  setEcho(id, data, parentArr,childrenArr).then(resId => {
    let changId =null,
    setValue=[];
     changId = resId[0].ancestors;
    if(changId.indexOf(",") > -1){ //是否只有一个部门
     changId = changId.split(",");
     changId.splice(0, 1);
     changId.push(id) //得到部门id
     parentArr = [];
     childrenArr=[];
    changId.forEach(v => { //遍历soreId，得到对应部门
       setEcho( v,data, parentArr,childrenArr).then(value => {
        setValue.push(...value);
       })
     })
    setTimeout(()=>{
      resolve(setValue)
    },0)
    }else{
       let sortValue= [];
        sortValue.push(resId[0])
         resolve(sortValue)
    }
  })
 })
}

//处理根据sortId ，得到部门信息
function setEcho(id,data, parentArr,childrenArr) {
  return new Promise((resolve, reject) => {
    if (data.length > 0) {
      data.forEach((value) => {
         if (value.children !== undefined) {
           setEcho(id,value.children,parentArr,childrenArr);
           if (value.deptId == id) {
              childrenArr.push(value);
           }
         }else{
           if (value.deptId== id) {
               parentArr.push(value)
           }
         }
      })
    }
    resolve(parentArr.concat(childrenArr))
  })

}

/**
 * 翻译-》多条
 * dictData 字典数据
 * needData 翻译数据
 * needStr 翻译内容
 *  strCn 赋值字段
 *  dictValue 匹配值
 * dictLabel 匹配中文
 */
 export function TranslateList(needData, data) {
  return new Promise(resolve => {
    if (needData.length > 0 && data.length > 0) {
      needData.forEach((item) => {
        data.forEach((obj) => {
          obj.dictData.forEach((value)=>{
            if (item[obj.needStr] == value[obj.dictValue]) {
              item[obj.strCn] = value[obj.dictLabel];
            }
          })
        })
      })
    }
    resolve(needData)
  })
}