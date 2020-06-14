BitCoinJDemo是一个基于[BitCoinJ](https://bitcoinj.github.io/)和[ZBar](https://github.com/yanzhenjie/android-zbar-sdk)两个工具库的安卓端分层确定性多重签名比特币钱包

### 项目描述
本项目为分层确定性多重签名比特币钱包的实现，其中的主要交易功能由BitcoinJ库实现，而与二维码相关的辅助功能则由ZBar实现（需要注意的是在实现二维码扫描功能时相关的jniLibs与drawable图像资源导入项目的先后顺序可能会影响到功能的实现）。

其功能以及特性大致如下：
* 可以通过配置选择来交易真实比特币或[TestNet3比特币](https://bitcoinfaucet.uo1.net/)，后者不具有实际价值，仅作测试使用
* 使用SPV技术来完成区块链记录的同步与存储，大幅提升了同步速度、减少了存储所需空间
* 使用分层确定性密钥技术，大幅减少了密钥存储所需空间，同时每个接受密钥都会在使用一段时间后更换，保障用户隐私
* 可以将密钥转换为助记词及时间，便于设备丢失后钱包的恢复
* 可以使用多重签名技术，即一个钱包中的钱款为多个密钥所共有，当其中的用户试图消费时需要获得一定量的其他拥有者的签名，需要注意的是该功能当前处于测试阶段，只能进行本地展示，实际使用还需要后续改进

### 项目构成及运行逻辑
![流程图](https://github.com/suyuyi/BitCoinJDemo/blob/master/%E6%95%88%E6%9E%9C%E5%B1%95%E7%A4%BA/%E9%A1%B9%E7%9B%AE%E9%80%BB%E8%BE%91.jpg)
* json、wallet是存储相关信息的本地存储文件，其他矩形为实际的代码
* 两种虚线，一种代表第一次创建钱包时的创建行为，而另一种则表示多重签名钱包载入时需要从json文件中读取此前保存的信息
* 关于单签名还是多签名的判断实际上在config阶段已经完成，图中的菱形框是一种逻辑上的表示，并不存在实际代码

### 项目具体代码构成和功能
#### MainActivity
> ##### 函数功能
> 该函数为初始界面函数，用户可以在该界面点击选择三种行为：创建、载入、恢复钱包，点击后随即跳转至相关界面
> ##### 函数接口：无
> ##### 相关界面（layout）：activity1
#### creat_config
> ##### 函数功能
> 为钱包的创建配置格式化的输入，其接收用户输入的字符串、比特币网络选择、多重签名选择三个信息，同时搜索是否存在同名文件，如果不存在，则根据用户的选择来决定之后跳转的函数，并将命名、网络选择以及模式传入至选定的函数中，需要注意的是此时的命名会在输入命名的基础上加上两个后缀来表示其网络和签名配置，便于解决同名不同配置造成后续操作出现问题的情况（决定函数类型的是多重签名选择，依据这一选择会决定之后进入单签名钱包还是进入具体的多重签名配置）
> ##### 函数接口：无
> ##### 相关界面：config_wallet
#### load_config
> ##### 函数功能
> 为钱包载入配置格式化输入，用户需要在该界面输入需要载入的钱包名称以及相关网络、签名选项，该函数会首先根据配置在输入名称后加上后缀，并以此检查是否存在同名文件，如果存在则可以载入，随后函数会根据签名选择的不同选定不同的跳转函数，并将将处理过名称、模式等信息传入其中，而对于多重签名函数而言，该函数还会搜索一个存有签名数量和签名数量要求的json文件，并将相关要求传入后续活动中
> ##### 函数接口：无
> ##### 相关界面：load_wallet
#### restore_wallet
> ##### 函数功能
> 为钱包恢复配置格式化输入，永华需要在该界面输入名称、助记词、创建时间，并选择网络与签名数量，函数会首先根据配置为名称增加后缀并搜寻是否存在同名钱包（注意是以.wallet为后缀的钱包文件，而非.json格式的配置文件，因此对于多重签名钱包而言会出现json文件存在而wallet文件不存在并成功使用该函数的情况，这一情况也正是我所设想的而并非错误，之后的恢复需要用的这一同名的json文件），如果不存在则根据选定的签名数量（单or多）来选定后续的活动，并将助记词、时间、模式、名称传入其中
> ##### 函数接口：无
> ##### 相关界面：load_seed_config
#### pre_multi
> ##### 函数功能
> 该函数主要为多重签名钱包提供关于签名总数和最低所需数量的配置，用户在选择多重签名钱包后，无论是创建、载入还是恢复都会先进入该函数，因为载入已经存在相关文件，因此会直接跳过；而恢复模式下函数会搜索存储目录下的同名json文件，并读取相关配置，随后将传入的配置加上其他需要追随(following)的密钥传入multi_v2函数中，正式开始恢复钱包；而创建模式下需要用户手动输入追随密钥数量和所需最少签名，以此为基础会生成对应数量的追随密钥并按照恢复模式一样传递给multi_v2，同时函数还会创建之前用到的json文件，其以传递进入的名称命名并存储用户输入的两个参数
> ##### 函数接口：通过intent以键值对的形式传入参数，全部参数为：mode、testnet、name、word、time
> ##### 相关界面：pre_mul_config
#### single
> ##### 函数功能
> 该函数为单签名钱包的主要实现模块，可以通过mode的不同来执行不同的模式，其接收mode、name、testnet三个传入值（如果mode：restore的话还需包含word、time），并将其传入start_wallet函数，初始化钱包配置并开始同步；当钱包同步至当前日期后即可开始使用（没同步完成时也可以点击各个按钮，但无法查看相关发送与接收是否完成，因此还是需要等到同步至当前）
> ##### 内部函数
> * receive（根据接收地址生成二维码）
> * scan(调用摄像头并扫描二维码将返回结果填入send_address框中)
> * send（发送比特币）
> * show_seed（调用show_str_seed函数展示恢复所需的助记词和创建时间）
> * show_all（展示所有交易记录）
> * refreshMSG（定期刷新余额、同步进度等信息）
> * showTwo（send发送时进一步要求用户确认）
> * show_loading（在钱包初始化的过程中相关点击按钮已经创建，但其函数调用的实体可能还未创建完毕，因此此时如果点击按钮会导致程序崩溃，所以最初几秒内会出现初始化界面防止用户点击）
> ##### 函数接口：通过intent以键值对形式传递参数，其全部参数为：name、mode、testnet（word、time）
> ##### 相关界面：activity_single
#### multi_v2
> ##### 函数功能
> 该函数为多重签名钱包的主要实现模块，可以通过mode的不同来执行不同的模式，其接收name、mode、testnet、key_cnt、 followingkey、threshold（如果mode：restore的话还需包含word、time）参数，其基本过程与single相似——此前的single模块管理一个密钥串，而多重签名则管理着一个由多个密钥混合生成的密钥串，由于本项目仅为功能展示，因此候选密钥的顺序是固定的（与pre_multi中生成的密钥一致），因此根据传入的密钥数量就可判断用到了哪些密钥，并以此为基础生成混合密钥链同时在本地添加TransactionSigner（BitCoinJ自带的类，可以在send的过程中被调用签名）以便于后续的支付，而完成混合密钥生成与Signer添加后，其运行过程与上述单签名钱包(single)相同
> ##### 函数接口：通过intent以键值对形式传递参数，其全部参数为：name、mode、testnet、key_cnt、 followingkey、threshold
> ##### 相关界面：activity_single
#### 开发环境
> ##### 代码开发环境如下：
> Android Studio 3.5.3  
> Build #AI-191.8026.42.35.6010548, built on November 15, 2019  
> JRE: 1.8.0_202-release-1483-b03 amd64  
> JVM: OpenJDK 64-Bit Server VM by JetBrains s.r.o  
> Windows 10 10.0  
> ##### 软件测试环境如下：
> 手机型号：Honor 8X  
> Android版本：10  
> EMUI版本：10.0.0  
#### 其他改进方向、思路
##### 在多重签名的创建、恢复过程中存在改进之处
> 关于交易的实现部分依赖于BitCoinJ库，该库使用过程如下：
> * 在一个线程中创建WalletAppKit类，该类包含比特币钱包所需要的全部功能，只需要将参数（network、name）配置好即可，随后使用类内部的startAsync()开始同步
> * 同步完毕后可以调用balance等查看当前钱包余额
> * 当需要花费余额是，直接构造包含金额和目标地址的SendRequest类
> * 调用kit.wallet().commitTx(request.tx)，该函数会从余额中挑选出符合要求的未花费输出并自动完成签名
> * 调用广播函数 kit.peerGroup().broadcastTransaction(request.tx).broadcast();将交易广播至全网，随后等待交易上链即可  

> 在这一过程中如果发生余额不足、交易失败等情况commitTx函数会弹出相关错误代码，据此可以弹出Toast来告知用户相关错误，多重签名钱包的交易过程也与此类似,但问题在于：单签名钱包只与当前用户相关，因此无论是其密钥只与当前用户有关，恢复过程只需恢复单个密钥即可，签名同样如此。  
> 而多重签名则不同，用户记忆的助记词等信息只与其自己的密钥有关，而钱包所用的密钥实际上是用户自己的密钥和其他密钥共同生成的混合密钥，因此在恢复过程中除了恢复自己的密钥还需要得到其他用户的公钥，而在签名过程中除了使用自己的密钥签名，还需要将sendrequest交给其他用户签名，最终合并这些签名使其数量超过threshold才能完成交易。  
> 本项目对于这部分的实现并不完整：通过在本地存储一连串的密钥（multi_v2中的init_cus_key()、pre_multi中的init_key()）在配置时根据所需密钥的数量传入相应的密钥，而在签名时同样根据相关数量来挨个使用密钥签名。  
> 因此如果后续进行改进，将多重签名的相关功能搬到线上，那么需要对pre_multi进行改动，使其传入multi_v2的followingkey来自于切实的其他用户，同时multi_v2中的TransactionSigner的添加也需要进行改动，使其确实与其他节点发生联系并收到其他节点传回的签名，同时对于多重签名的恢复也需要做出改进，需要将用户助记词、时间与相关配置（密钥数量、所需最少签名数量）进行结合，而不是一个简单的本地json文件。  
