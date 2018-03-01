# RxSerialPort
基于Rxjava2.x的串口通信library 
## 使用方法

打开并监听串口，accept方法会一直保持回调，直到手动关闭串口

        try {
            SerialPortListener.statListen("/dev/ttyS3", 9600, 0)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<byte[]>() {
                        @Override
                        public void accept(byte[] bytes) throws Exception {
                            //todo 实现业务逻辑
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


发送数据

	SerialPortSender.send(serialPortFileName,bytes);

关闭串口

	SerialPortListener.stop(serialPortFileName);
