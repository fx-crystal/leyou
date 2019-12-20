package com.leyou.order.service;

import com.leyou.common.pojo.UserInfo;
import com.leyou.order.interceptor.LoginInterceptor;
import com.leyou.order.mapper.AddressMapper;
import com.leyou.order.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class AddressService {
    @Autowired
    private AddressMapper addressMapper;

    public void deleteAddress(Long addressId) {
        UserInfo userInfo = LoginInterceptor.getUser();
        Example example = new Example(Address.class);
        example.createCriteria().andEqualTo("userId",userInfo.getId()).andEqualTo("id",addressId);
        this.addressMapper.deleteByExample(example);
    }

    public void updateAddressByUserId(Address address) {
        UserInfo userInfo = LoginInterceptor.getUser();
        address.setUserId(userInfo.getId());
        setDefaultAddress(address);
        this.addressMapper.updateByPrimaryKeySelective(address);

    }

    public List<Address> queryAddressByUserId() {
        UserInfo userInfo = LoginInterceptor.getUser();
        Example example = new Example(Address.class);
        example.createCriteria().andEqualTo("userId",userInfo.getId());
        return this.addressMapper.selectByExample(example);
    }

    public void addAddressByUserId(Address address) {
        UserInfo userInfo = LoginInterceptor.getUser();
        address.setUserId(userInfo.getId());
        setDefaultAddress(address);
        this.addressMapper.insert(address);
    }

    public Address queryAddressById(Long addressId) {
        UserInfo userInfo = LoginInterceptor.getUser();
        Example example = new Example(Address.class);
        example.createCriteria().andEqualTo("id",addressId).andEqualTo("userId",userInfo.getId());
        return this.addressMapper.selectByExample(example).get(0);
    }

    public void setDefaultAddress(Address address){
        if (address.getDefaultAddress()){
            //如果将本地址设置为默认地址，那么该用户下的其他地址都应该是非默认地址
            List<Address> addressList = this.queryAddressByUserId();
            addressList.forEach(addressTemp -> {
                if (addressTemp.getDefaultAddress()){
                    addressTemp.setDefaultAddress(false);
                    this.addressMapper.updateByPrimaryKeySelective(addressTemp);
                }
            } );
        }
    }
}
