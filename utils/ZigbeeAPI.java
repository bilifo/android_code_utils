package utils;

public class ZigbeeAPI {
	byte type=0x02;//设备类型
	byte panl=(byte) 0xfe;//网络ID低8位
	byte panh=(byte) 0xff;//网络ID高8位
	byte addrl=(byte) 0xff;//局域网节点短地址低8位
	byte addrh=(byte) 0xff;//局域网节点短地址高8位
	byte chan=0x0f;//信道
	byte ver=0x01;//软件版本
	//int key;//通信秘钥
	byte prd=0x05;//允许入网时间，单位：秒
	byte pollrate=0x00;//终端数据轮询时间周期，单位：100毫秒
	byte ADl;//
	byte ADh;//
	byte status;//表示IO状态
	byte mod;//PWM频率值
	byte PWMxL;//
	byte PWMxH;//
	long ieee;//长地址，八个 字节 ，每个模块的长地址长地址出厂都是 唯一的

	private void PTsend(byte[] cmd){
		byte len=(byte) cmd.length;
		//发送出去
		
	}
	
	//获取设备类型
	public void zigbee_getStyle(){
		//串口返回来的值
		this.type=type;
	}
	
	//设置设备类型	type----类型,取值为0（主设备，协调器）,1（路由）,2（节点，终端）
	public void zigbee_setStyle(byte type){
		this.type=type;
		
	}
	
	//获取网络号
	public void zigbee_getNetwork(){
		
	}
	
	//设置网络ID   panl----低字节  panh----高字节
	public void zigbee_setNetwork(byte panl,byte panh){
		this.panh=panh;
		this.panl=panl;
		
	}
	
	//获取设置短地址
	public void zigbee_getAddr(){
		
	}
	
	//获取通讯信道
	public void zigbee_getChannel(byte addrl,byte addrh){
		this.addrl=addrl;
		
	}
	
	//设置通讯信道  chan----信道 范围0x00~0x0f,只有协调器可以设置
	public void zigbee_setChannel(byte chan){
		this.addrh=addrh;
		
	}
	
	//获取终端节点数据轮询周期
	public void zigbee_getNodeCycle(){
		
	}
	
	//设置终端节点数据轮询周期   pollrate---表示终端节点数据轮询时间周期，一个字节，单位是100毫秒，范围是0x01~0x32，0x00表示不轮询。只有协调可以设置
	public void zigbee_setNodeCycle(byte pollrate){
		this.pollrate=pollrate;
	}
	
	//获取设备长地址（IEEE）IEEE[]----ieee地址
	public void zigbee_getIEEE(){
		
	}
	
	//恢复出厂设置	
	public void zigbee_FactoryReset(){
		
	}
	
	//设置入网许可   prd----表示允许入网的时间周期，单位是秒，范围0x01~0xfe,0x00表示关闭允许，0xff表示打开允许。1字节，只有协调器可以设置
	public void zigbee_NetAllows(byte prd){
		this.prd=prd;
	}
	
//	//设置网络秘钥 key[]----秘钥 16字节，只有协调器可设置
//	public void zigbee_NetKey(){
//		
//	}
	
	//重启设备
	public void zigbee_Restart(byte addrl,byte addrh){
		
	}
	
	//获取节点长地址
	public void zigbee_NodeIEEE(byte addrl,byte addrh){
		
	}
	
	//设置节点重启
	public void zigbee_NodeFactoryReset(byte addrl,byte addrh){
		
	}
	
	//设置节点恢复出厂设置
	public void zigbee_NodeRestart(byte addrl,byte addrh){
		
	}
	
	
}
