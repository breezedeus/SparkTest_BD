
# 编译环境推荐

* Spark1.2.0
* Scala2.10.x
* Jdk1.6
* IntelliJ IDEA14.0.2 (+ plugin scala)


# 内容

目前只包含了以下两个使用案例：

* MLlib中的Logistic Regression模型
	* SGD求解
	* LBFGS求解
* GraphX中计算与每个结点距离为`K`的所有邻居，可用于产生推荐候选集（可取`K=3`）。


# 使用方法

## 类`MLAppLR`

## 类`GraphXApp`