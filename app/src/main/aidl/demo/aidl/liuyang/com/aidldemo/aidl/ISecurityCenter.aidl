// ISecurityCenter.aidl
package demo.aidl.liuyang.com.aidldemo.aidl;

// Declare any non-default types here with import statements

interface ISecurityCenter {
    String encrypt(in String content);
    String decrypt(in String content);
}
