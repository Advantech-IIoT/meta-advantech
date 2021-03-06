DESCRIPTION = "generate sd-card image"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
SECTION = "BSP"

MK_SDCARD_IMG = "mk-sdcard-image"

inherit deploy

SRC_URI = "file://mkimg-linux.sh"

S = "${WORKDIR}"

do_deploy() {
	install -d ${DEPLOYDIR}/${MK_SDCARD_IMG}
	install -d ${DEPLOYDIR}/${MK_SDCARD_IMG}/images
	install -d ${DEPLOYDIR}/${MK_SDCARD_IMG}/images/rootfs
	install -m 0755 ${S}/mkimg-linux.sh        ${DEPLOYDIR}/${MK_SDCARD_IMG}


	if [ -e ${DEPLOY_DIR_IMAGE}/Image ] ; then
		cp ${DEPLOY_DIR_IMAGE}/Image		${DEPLOYDIR}/${MK_SDCARD_IMG}/images/Image
		for dtb in ${KERNEL_DEVICETREE}; do
			DTB_BASE_NAME=`basename ${dtb}`
			cp ${DEPLOY_DIR_IMAGE}/${DTB_BASE_NAME}   ${DEPLOYDIR}/${MK_SDCARD_IMG}/images		
		done
	fi


	if [ -e ${DEPLOY_DIR_IMAGE}/imx-boot ] ; then
		 cp ${DEPLOY_DIR_IMAGE}/imx-boot        ${DEPLOYDIR}/${MK_SDCARD_IMG}/images/flash.bin

	fi

        if [ -e ${DEPLOY_DIR_IMAGE}/swupdate-image-${MACHINE}.cpio.gz.u-boot ] ; then
		cp ${DEPLOY_DIR_IMAGE}/swupdate-image-${MACHINE}.cpio.gz.u-boot ${DEPLOYDIR}/${MK_SDCARD_IMG}/images/initrd.img
        fi


        if [ -e ${DEPLOY_DIR_IMAGE}/imx-image-full-${MACHINE}.tar.bz2 ] ; then
		cp ${DEPLOY_DIR_IMAGE}/imx-image-full-${MACHINE}.tar.bz2 ${DEPLOYDIR}/${MK_SDCARD_IMG}/images/rootfs/

        fi
	
}

do_after_deploy() {

	if [ -e ${DEPLOY_DIR_IMAGE}/${MK_SDCARD_IMG}/images/rootfs/imx-image-full-${MACHINE}.tar.bz2 ] ; then
		tar jxvf ${DEPLOY_DIR_IMAGE}/${MK_SDCARD_IMG}/images/rootfs/imx-image-full-${MACHINE}.tar.bz2 -C ${DEPLOY_DIR_IMAGE}/${MK_SDCARD_IMG}/images/rootfs
		rm -rf ${DEPLOY_DIR_IMAGE}/${MK_SDCARD_IMG}/images/rootfs/imx-image-full-${MACHINE}.tar.bz2
	fi
	cd ${DEPLOY_DIR_IMAGE}/${MK_SDCARD_IMG}/
	bash -c ${DEPLOY_DIR_IMAGE}/${MK_SDCARD_IMG}/mkimg-linux.sh
	rm -rf ${DEPLOY_DIR_IMAGE}/${MK_SDCARD_IMG}/images/
	rm -rf ${DEPLOY_DIR_IMAGE}/${MK_SDCARD_IMG}/mkimg-linux.sh
}


addtask deploy after do_compile
addtask after_deploy after do_deploy before do_build
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_package[noexec] = "1"
do_populate_sysroot[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_qa[noexec] = "1"
do_package_write_deb[noexec] = "1"
