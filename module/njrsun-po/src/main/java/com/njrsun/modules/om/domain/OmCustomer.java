package com.njrsun.modules.om.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.njrsun.common.annotation.Excel;

/**
 * 客户列对象 om_customer
 * 
 * @author njrsun
 * @date 2021-06-24
 */
public class OmCustomer
{
    private static final long serialVersionUID = 1L;

    /** 客户序号 */
    private Long uniqueId;

    /** 客户名称 */
    @Excel(name = "客户名称")
    private String name;

    /** 客户简称 */
    @Excel(name = "客户简称")
    private String shortName;
    @Excel(name = "客户别名")
    private String alias;
    /** 公司地址 */
    @Excel(name = "公司地址")
    private String addr;
    /** 邮编 */
    @Excel(name = "邮编")
    private String postcode;

    /** 电话 */
    @Excel(name = "电话")
    private String tel;

    /** 开户行 */
    @Excel(name = "开户行")
    private String bank;

    /** 账号 */
    @Excel(name = "账号")
    private String account;

    /** 税号 */
    @Excel(name = "税号")
    private String tax;

    /** 地址电话 */
    @Excel(name = "地址电话")
    private String addr2;

    @Excel(name = "所在省")
    private String province;
    @Excel(name = "所在市ID")
    private String cityId;
    @Excel(name = "所在市")
    private String city;
    @Excel(name = "信用等级")
    private String credit;
    @Excel(name = "信用额度")
    private Double creditLimit;

    @Excel(name = "分类")
    private String  type;
    @Excel(name = "手机号")
    private String wMobile;
    @Excel(name = "业务联系人")
    private String wContacts;

    @Excel(name = "手机号")
    private String fMobile;
    @Excel(name = "财务联系人")
    private String fContacts;
    @Excel(name = "备注")
    private String remark;
    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    private String createBy;

    private String updateBy;
    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    /** 版本号 */
    @Excel(name = "版本号")
    private Long version;

    /** 删除标志 */
    private Long delFlag;

    public void setUniqueId(Long uniqueId) 
    {
        this.uniqueId = uniqueId;
    }

    public Long getUniqueId() 
    {
        return uniqueId;
    }
    public void setShortName(String shortName) 
    {
        this.shortName = shortName;
    }

    public String getShortName() 
    {
        return shortName;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setAddr(String addr) 
    {
        this.addr = addr;
    }

    public String getAddr() 
    {
        return addr;
    }
    public void setPostcode(String postcode) 
    {
        this.postcode = postcode;
    }

    public String getPostcode() 
    {
        return postcode;
    }
    public void setTel(String tel) 
    {
        this.tel = tel;
    }

    public String getTel() 
    {
        return tel;
    }
    public void setBank(String bank) 
    {
        this.bank = bank;
    }

    public String getBank() 
    {
        return bank;
    }
    public void setAccount(String account) 
    {
        this.account = account;
    }

    public String getAccount() 
    {
        return account;
    }
    public void setTax(String tax) 
    {
        this.tax = tax;
    }

    public String getTax() 
    {
        return tax;
    }
    public void setAddr2(String addr2) 
    {
        this.addr2 = addr2;
    }

    public String getAddr2() 
    {
        return addr2;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setUpdateDate(Date updateDate) 
    {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() 
    {
        return updateDate;
    }
    public void setVersion(Long version) 
    {
        this.version = version;
    }

    public Long getVersion() 
    {
        return version;
    }
    public void setDelFlag(Long delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Long getDelFlag() 
    {
        return delFlag;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getwMobile() {
        return wMobile;
    }

    public void setwMobile(String wMobile) {
        this.wMobile = wMobile;
    }

    public String getwContacts() {
        return wContacts;
    }

    public void setwContacts(String wContacts) {
        this.wContacts = wContacts;
    }

    public String getfMobile() {
        return fMobile;
    }

    public void setfMobile(String fMobile) {
        this.fMobile = fMobile;
    }

    public String getfContacts() {
        return fContacts;
    }

    public void setfContacts(String fContacts) {
        this.fContacts = fContacts;
    }


    public String getRemark() {
        return remark;
    }


    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Override
    public String toString() {
        return "OmCustomer{" +
                "uniqueId=" + uniqueId +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", alias='" + alias + '\'' +
                ", addr='" + addr + '\'' +
                ", postcode='" + postcode + '\'' +
                ", tel='" + tel + '\'' +
                ", bank='" + bank + '\'' +
                ", account='" + account + '\'' +
                ", tax='" + tax + '\'' +
                ", addr2='" + addr2 + '\'' +
                ", cityId='" + cityId + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", credit='" + credit + '\'' +
                ", creditLimit=" + creditLimit +
                ", type='" + type + '\'' +
                ", wMobile='" + wMobile + '\'' +
                ", wContacts='" + wContacts + '\'' +
                ", fMobile='" + fMobile + '\'' +
                ", fContacts='" + fContacts + '\'' +
                ", remark='" + remark + '\'' +
                ", status='" + status + '\'' +
                ", updateDate=" + updateDate +
                ", version=" + version +
                ", delFlag=" + delFlag +
                '}';
    }
}
