package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name="tb_spec_param")
public class SpecParam {

    @Id
    @KeySql(useGeneratedKeys = true)
    private long id;
    private String name;
    private Long cid;
    private Long groupId;
    @Column(name="`numeric`")
    private Boolean numeric;
    private String unit;
    private boolean generic;
    private boolean searching;
    private String segments;

}
