BitCoinJDemo是一个基于[BitCoinJ](https://bitcoinj.github.io/)和[ZBar](https://github.com/yanzhenjie/android-zbar-sdk)两个工具库的安卓端分层确定性多重签名比特币钱包
###项目描述
本项目为分层确定性多重签名比特币钱包的实现，其中的主要交易功能由BitcoinJ库实现，而与二维码相关的辅助功能则由ZBar实现（需要注意的是在实现二维码扫描功能时相关的jniLibs与drawable图像资源导入项目的先后顺序可能会影响到功能的实现）
