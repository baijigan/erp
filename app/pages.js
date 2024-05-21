const { hot } = require('uni-pages-hot-modules')
module.exports = hot((pagesJson) => {
    let basePages = []
    let baseSubPackages = []

		// npm i uni-pages-hot-modules -S
    return {
        // 合并pages.json的内容
        ...pagesJson,
        pages:[
            ...basePages,
            ...require('./pages/pages.js'),
        ],
        subPackages:[
            ...baseSubPackages,
            ...require('./module/datav/pages.js'),
						...require('./module/plan/pages.js'),
						...require('./module/production/pages.js'),
						...require('./module/purchase/pages.js'),
						...require('./module/sales/pages.js'),
						...require('./module/stock/pages.js'),
        ]				
    }
})