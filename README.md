
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
目前只包含了以下两个使用案例。

## 类`MLAppLR`
MLlib中的Logistic Regression模型，支持两种求解方法：SGD和LBFGS。支持的所有参数如下：

* --train=<train_file>：训练集对应的文件名称
* --test=<test_file>：测试集对应的文件名称
* --algName=<algorithm_name>：值为`lbfgs`时使用LBFGS优化方法进行求解，调用的是MLlib中的`LogisticRegressionWithLBFGS`；其他值时使用mini-batch SGD优化方法，调用的是MLlib中的`LogisticRegressionWithSGD`；默认值为`sgd`
* --numIterations=<number_of_max_iterations>：最大迭代次数；默认值为`50`
* --regParam=<L2_reguralization>：L2惩罚项的系数；默认值为`0.1`
* --stepSize=<step_size>：只针对SGD，表示SGD中的步长；默认值为`0.1`
* --convergenceTol=<convergence_tolerance>：只针对LBFGS，表示LBFGS中的收敛标准；默认值为`1e-5`
* --miniBatchFraction=<mini_batch_fraction>：只针对SGD，表示SGD每次迭代使用的mini batch数量占总样本集的比例；默认值为`0.01`


## 类`GraphXApp`