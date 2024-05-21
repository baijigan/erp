const Mock = require('mockjs')

const data = Mock.mock({
  'items|6': [{
    'id|+1': 1,
    value: '@integer(1, 50)'
  }]
})

const buf= {
  xData: ["产品1", "产品2", "rose3", "rose4", "rose5", "rose6"],
  seriesData: [
    { value: 10, name: "产品1" },
    { value: 5, name: "产品2" },
    { value: 15, name: "rose3" },
    { value: 25, name: "rose4" },
    { value: 20, name: "rose5" },
    { value: 35, name: "rose6" }
  ]
}

module.exports = [
  {
    url: '/bulletin/s1/centerleft1',
    type: 'get',
    response: () => {
      return {
        code: 20000,
        data: buf
      }
    }
  }
]
