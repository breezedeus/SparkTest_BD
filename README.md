# 文档
这是我在部门内部的[Spark分享slides](http://pan.baidu.com/s/1nrscQ)，内容从IDE到Spark里最重要的一些基本概念。


# 编译环境推荐

* Spark1.2.0
* Scala2.10.x
* Jdk1.6
* IntelliJ IDEA14.0.2 (+ plugin scala)


# 使用方法
目前只包含了以下两个功能类。

## 类`MLAppLR`
机器学习中的Logistic Regression模型，支持两种求解方法：SGD和LBFGS。支持的所有参数如下：

* --train=\<train_file\>：训练集对应的文件名称
* --test=\<test_file\>：测试集对应的文件名称
* --output=\<output_file\>：把测试集上的预测结果存入指定目录；每行格式为`<pred_value> <real_label>`；如果目录已经存在，需要先删除；此参数可不传，不传的话就不输出预测结果
* --algName=\<algorithm_name\>：值为`lbfgs`时使用LBFGS优化方法进行求解，调用的是MLlib中的`LogisticRegressionWithLBFGS`；其他值时使用mini-batch SGD优化方法，调用的是MLlib中的`LogisticRegressionWithSGD`；默认值为`sgd`
* --numIterations=\<number\_of\_max\_iterations\>：最大迭代次数；默认值为`50`
* --regParam=\<L2_reguralization\>：L2惩罚项的系数；默认值为`0.1`
* --stepSize=\<step_size\>：只针对SGD，表示SGD中的步长；默认值为`0.1`
* --convergenceTol=\<convergence_tolerance\>：只针对LBFGS，表示LBFGS中的收敛标准；默认值为`1e-5`
* --miniBatchFraction=\<mini\_batch\_fraction\>：只针对SGD，表示SGD每次迭代使用的mini batch数量占总样本集的比例；默认值为`0.01`

最后会返回在测试集上的`AUC`。

###使用示例

```
$ spark-submit --master local[2] --class MLAppLR out/artifacts/ScalaTest4_jar/ScalaTest4.jar --train=data/svmguide1 --test=data/svmguide1.t -algName=lbfgs -output=data/svmguide1.output
```


## 类`GraphXApp`
GraphX中计算与每个结点距离为`K`的所有邻居，可用于产生推荐候选集（可取`K=3`）。支持的所有参数如下：

* --edgeFile=\<edge_file\>：存储图中所有边的文件；每行的格式为`<src_id> <dst_id>`
* --output=\<output_file\>：获得的图上指定距离的邻居结果存入指定目录；如果目录已经存在，需要先删除；此参数可不传，不传的话就不输出计算结果

* --pathLength=\<path_length\>：路径长度，即`K`；默认值为`1`


###使用示例

```
$ spark-submit --master local[2] --class GraphXApp out/artifacts/ScalaTest4_jar/ScalaTest4.jar --edgeFile=data/graphxapp --pathLength=3 -output=data/graphxapp.output
```