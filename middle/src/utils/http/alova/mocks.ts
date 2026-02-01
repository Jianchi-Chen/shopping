// 这里按需导入 mock 文件，只有在这里导入并导出，才会执行 mock 拦截
// 跟根据实际开发情况配置
import UserMock from '../../../../mock/user';
import MenusMock from '../../../../mock/user/menus';
import ConsoleMock from '../../../../mock/dashboard/console';
import TableMock from '../../../../mock/table/list';
import SystemMenuMock from '../../../../mock/system/menu';
import SystemRoleMock from '../../../../mock/system/role';
import CommerceProductMock from '../../../../mock/commerce/products';
import CommerceOrderMock from '../../../../mock/commerce/orders';
import IdentityMerchantMock from '../../../../mock/identity/merchants';
import IdentityUserMock from '../../../../mock/identity/users';

export default [
	UserMock,
	MenusMock,
	TableMock,
	ConsoleMock,
	SystemMenuMock,
	SystemRoleMock,
	CommerceProductMock,
	CommerceOrderMock,
	IdentityMerchantMock,
	IdentityUserMock,
];
