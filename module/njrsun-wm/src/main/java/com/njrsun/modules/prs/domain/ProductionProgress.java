package com.njrsun.modules.prs.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author njrsun
 * @create 2022/1/5 10:30
 */
@Data
public class ProductionProgress {

   private   List<String> productionLine = new ArrayList<>();

   private   List<PrsWorkData> prsWorkData = new ArrayList<>() ;

   private List<PrsOrderMaster> detail = new ArrayList<>();

}
