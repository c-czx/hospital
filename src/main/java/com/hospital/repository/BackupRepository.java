/**
 * 本代码由 Muyvge 编写，仅用于学习交流，不得用于商业用途
 * 本代码的使用、复制、修改、合并、发布、分发、再许可、销售等行为均受到法律的严格限制。
 */
package com.hospital.repository;

import com.hospital.entity.Backup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 备份数据访问层
 * 提供备份记录的 CRUD 操作
 */
@Repository
public interface BackupRepository extends JpaRepository<Backup, Long> {

    /**
     * 按备份时间倒序查询所有备份记录
     * @return 备份记录列表
     */
    List<Backup> findAllByOrderByBackupTimeDesc();

    /**
     * 根据备份类型查询备份记录
     * @param backupType 备份类型
     * @return 备份记录列表
     */
    List<Backup> findByBackupTypeOrderByBackupTimeDesc(String backupType);

    /**
     * 根据状态查询备份记录
     * @param status 备份状态
     * @return 备份记录列表
     */
    List<Backup> findByStatusOrderByBackupTimeDesc(String status);
}
