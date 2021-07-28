### pomangam-api-monilith


### 디렉토리 구조

#### Domain 디렉토리

```
domain
	├─common
	│  ├─cmap
	│  ├─fcm
	│  ├─file
	│  ├─kakao
	│  └─oauth
	│      ├─configuration
	│      ├─interceptor
	│      └─service
	├─cs
	│  ├─faq
	│  │  ├─category
	│  │  ├─faq
	│  │  └─mapper
	│  ├─notice
	│  │  ├─mapper
	│  │  └─notice
	│  └─policy
	├─delivery
	│  ├─deliverysite
	│  ├─detailsite
	│  └─region
	├─marketing
	│  ├─advertisement
	│  │  ├─advertisement
	│  │  └─mapper
	│  ├─coupon
	│  │  ├─coupon
	│  │  └─mapper
	│  ├─event
	│  │  ├─event
	│  │  └─mapper
	│  ├─point
	│  │  ├─log
	│  │  └─rank
	│  └─promotion
	│      ├─mapper
	│      └─promotion
	├─order
	│  ├─item
	│  │  ├─item
	│  │  └─sub
	│  ├─log
	│  ├─order
	│  ├─ordertime
	│  │  ├─mapper
	│  │  └─ordertime
	│  └─payment
	│      └─vbank
	├─product
	│  ├─category
	│  ├─image
	│  ├─like
	│  ├─product
	│  ├─reply
	│  │  ├─like
	│  │  └─reply
	│  └─sub
	│      ├─category
	│      ├─image
	│      ├─mapper
	│      └─sub
	├─staff
	├─store
	│  ├─category
	│  ├─image
	│  ├─like
	│  ├─mapper
	│  ├─owner
	│  ├─review
	│  │  ├─image
	│  │  ├─like
	│  │  ├─reply
	│  │  │  ├─like
	│  │  │  └─reply
	│  │  └─review
	│  ├─store
	│  └─story
	│      ├─image
	│      └─story
	└─user
```

<br/>

#### Global 디렉토리

```
global
	├─annotation
	├─configuration
	│  ├─database
	│  │  └─jpa
	│  ├─health
	│  ├─http
	│  ├─mapper
	│  └─rabbitmq
	├─error
	│  └─exception
	├─filter
	├─util
	│  ├─bizm
	│  │  └─template
	│  ├─bootpay
	│  │  └─model
	│  │      ├─request
	│  │      └─response
	│  │          ├─callback
	│  │          ├─cancel
	│  │          └─verify
	│  ├─choseong
	│  ├─formatter
	│  ├─queryrunner
	│  ├─reflection
	│  ├─security
	│  ├─sqlinjection
	│  ├─time
	│  └─validation
	│      └─annotation
	└─_base
```

<br/>
