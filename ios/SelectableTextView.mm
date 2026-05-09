#import "SelectableTextView.h"

#import <react/renderer/components/SelectableTextViewSpec/ComponentDescriptors.h>
#import <react/renderer/components/SelectableTextViewSpec/EventEmitters.h>
#import <react/renderer/components/SelectableTextViewSpec/Props.h>
#import <react/renderer/components/SelectableTextViewSpec/RCTComponentViewHelpers.h>

#import <React/RCTConversions.h>
#import <React/RCTMountingTransactionObserving.h>
#import "RCTFabricComponentsPlugins.h"

using namespace facebook::react;

@class SelectableTextView;

@interface SelectableUITextView : UITextView
@property (nonatomic, weak) SelectableTextView *parentSelectableTextView;
@end

@implementation SelectableUITextView

- (BOOL)canPerformAction:(SEL)action withSender:(id)sender
{
    if (self.parentSelectableTextView) {
        return [self.parentSelectableTextView canPerformAction:action withSender:sender];
    }
    return [super canPerformAction:action withSender:sender];
}

- (NSMethodSignature *)methodSignatureForSelector:(SEL)aSelector
{
    if (self.parentSelectableTextView) {
        NSMethodSignature *signature = [self.parentSelectableTextView methodSignatureForSelector:aSelector];
        if (signature) {
            return signature;
        }
    }
    return [super methodSignatureForSelector:aSelector];
}

- (void)forwardInvocation:(NSInvocation *)anInvocation
{
    if (self.parentSelectableTextView) {
        [self.parentSelectableTextView forwardInvocation:anInvocation];
    } else {
        [super forwardInvocation:anInvocation];
    }
}

@end

@interface SelectableTextView () <RCTMountingTransactionObserving, RCTSelectableTextViewViewProtocol>
@end

@implementation SelectableTextView {
    std::vector<std::string> _menuOptionsVector;
    NSMutableSet<NSNumber *> *_contentComponentTags;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<SelectableTextViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const SelectableTextViewProps>();
    _props = defaultProps;

    _textView = [[SelectableUITextView alloc] init];
    ((SelectableUITextView *)_textView).parentSelectableTextView = self;
    _textView.delegate = self;
    _textView.editable = NO;
    _textView.selectable = YES;
    _textView.scrollEnabled = NO;
    _textView.backgroundColor = [UIColor clearColor];
    _textView.textContainerInset = UIEdgeInsetsZero;
    _textView.textContainer.lineFragmentPadding = 0;
    _textView.userInteractionEnabled = YES;
    _textView.allowsEditingTextAttributes = NO;
    _textView.dataDetectorTypes = UIDataDetectorTypeNone;
    _textView.text = @"";
    _menuOptions = @[];
    _contentComponentTags = [NSMutableSet new];

    self.contentView = _textView;
    self.userInteractionEnabled = YES;
  }

  return self;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
    const auto &oldViewProps = *std::static_pointer_cast<SelectableTextViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<SelectableTextViewProps const>(props);

    if (oldViewProps.menuOptions != newViewProps.menuOptions) {
        _menuOptionsVector = newViewProps.menuOptions;

        NSMutableArray<NSString *> *options = [[NSMutableArray alloc] init];
        for (const auto& option : _menuOptionsVector) {
            [options addObject:[NSString stringWithUTF8String:option.c_str()]];
        }
        _menuOptions = options;
    }

    if (oldViewProps.selectionColor != newViewProps.selectionColor) {
        _textView.tintColor = RCTUIColorFromSharedColor(newViewProps.selectionColor);
    }

    [super updateProps:props oldProps:oldProps];
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    [self updateTextViewContent];
}

- (void)mountingTransactionDidMount:(const MountingTransaction &)transaction
               withSurfaceTelemetry:(const facebook::react::SurfaceTelemetry &)surfaceTelemetry
{
    (void)surfaceTelemetry;

    if ([self mountingTransactionAffectsTextContent:transaction]) {
        [self updateTextViewContent];
    }
}

- (BOOL)mountingTransactionAffectsTextContent:(const MountingTransaction &)transaction
{
    Tag ownTag = (Tag)self.tag;

    for (const auto &mutation : transaction.getMutations()) {
        if (mutation.parentTag == ownTag ||
            [self contentComponentTagsContainTag:mutation.parentTag] ||
            [self contentComponentTagsContainTag:mutation.oldChildShadowView.tag] ||
            [self contentComponentTagsContainTag:mutation.newChildShadowView.tag]) {
            return YES;
        }
    }

    return NO;
}

- (BOOL)contentComponentTagsContainTag:(Tag)tag
{
    if (tag <= 0) {
        return NO;
    }

    return [_contentComponentTags containsObject:@(tag)];
}

- (void)updateTextViewContent
{
    NSMutableAttributedString *combinedAttributedText = [[NSMutableAttributedString alloc] init];
    NSMutableSet<NSNumber *> *contentComponentTags = [NSMutableSet new];

    [self extractStyledTextFromView:self
               intoAttributedString:combinedAttributedText
                           hideViews:YES
                contentComponentTags:contentComponentTags];

    _contentComponentTags = contentComponentTags;
    _textView.attributedText = combinedAttributedText;
}

- (void)extractStyledTextFromView:(UIView *)view
             intoAttributedString:(NSMutableAttributedString *)attributedString
                         hideViews:(BOOL)hideViews
              contentComponentTags:(NSMutableSet<NSNumber *> *)contentComponentTags
{
    BOOL foundText = NO;

    if (view != self && view != _textView && view.tag > 0) {
        [contentComponentTags addObject:@(view.tag)];
    }

    if ([view respondsToSelector:@selector(attributedText)]) {
        NSAttributedString *attributedText = [view performSelector:@selector(attributedText)];
        if (attributedText && attributedText.length > 0) {
            [attributedString appendAttributedString:attributedText];
            foundText = YES;
        }
    }
    else if ([view isKindOfClass:[UILabel class]]) {
        UILabel *label = (UILabel *)view;
        if (label.attributedText && label.attributedText.length > 0) {
            [attributedString appendAttributedString:label.attributedText];
            foundText = YES;
        } else if (label.text && label.text.length > 0) {
            NSAttributedString *plainText = [[NSAttributedString alloc] initWithString:label.text];
            [attributedString appendAttributedString:plainText];
            foundText = YES;
        }
    }
    else if ([view respondsToSelector:@selector(text)]) {
        NSString *text = [view performSelector:@selector(text)];
        if (text && text.length > 0) {
            NSAttributedString *plainText = [[NSAttributedString alloc] initWithString:text];
            [attributedString appendAttributedString:plainText];
            foundText = YES;
        }
    }

    if (foundText && hideViews) {
        view.hidden = YES;
    }

    for (UIView *subview in view.subviews) {
        if (subview != _textView) {
            [self extractStyledTextFromView:subview
                       intoAttributedString:attributedString
                                   hideViews:hideViews
                        contentComponentTags:contentComponentTags];
        }
    }
}

#pragma mark - UITextViewDelegate

- (void)textViewDidChangeSelection:(UITextView *)textView
{
    if (textView.selectedRange.length > 0) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self showCustomMenu];
        });
    } else {
        [[UIMenuController sharedMenuController] hideMenuFromView:_textView];
    }
}

- (void)showCustomMenu
{
    if (![_textView canBecomeFirstResponder]) {
        return;
    }

    [_textView becomeFirstResponder];

    UIMenuController *menuController = [UIMenuController sharedMenuController];
    menuController.menuItems = nil;

    NSMutableArray<UIMenuItem *> *menuItems = [[NSMutableArray alloc] init];

    for (NSString *option in _menuOptions) {
        NSString *selectorName = [[option stringByReplacingOccurrencesOfString:@" " withString:@"_"]
                                                stringByReplacingOccurrencesOfString:@"[^a-zA-Z0-9_]"
                                                withString:@"_"
                                                options:NSRegularExpressionSearch
                                                range:NSMakeRange(0, option.length)];
        SEL action = NSSelectorFromString([NSString stringWithFormat:@"customAction_%@:", selectorName]);
        UIMenuItem *menuItem = [[UIMenuItem alloc] initWithTitle:option action:action];
        [menuItems addObject:menuItem];
    }

    menuController.menuItems = menuItems;
    [menuController update];

    CGRect selectedRect = [_textView firstRectForRange:_textView.selectedTextRange];
    if (!CGRectIsEmpty(selectedRect)) {
        [menuController showMenuFromView:_textView rect:selectedRect];
    }
}

- (BOOL)canBecomeFirstResponder
{
    return YES;
}

- (BOOL)canPerformAction:(SEL)action withSender:(id)sender
{
    if (action == @selector(copy:) || action == @selector(selectAll:)) {
        return YES;
    }
    NSString *selectorName = NSStringFromSelector(action);
    if ([selectorName hasPrefix:@"customAction_"] && [selectorName hasSuffix:@":"]) {
        return YES;
    }
    return NO;
}

#pragma mark - Dynamic method dispatch for custom menu actions

- (NSMethodSignature *)methodSignatureForSelector:(SEL)aSelector
{
    NSString *selectorName = NSStringFromSelector(aSelector);
    if ([selectorName hasPrefix:@"customAction_"] && [selectorName hasSuffix:@":"]) {
        return [NSMethodSignature signatureWithObjCTypes:"v@:@"];
    }
    return [super methodSignatureForSelector:aSelector];
}

- (void)forwardInvocation:(NSInvocation *)anInvocation
{
    NSString *selectorName = NSStringFromSelector(anInvocation.selector);

    if ([selectorName hasPrefix:@"customAction_"] && [selectorName hasSuffix:@":"]) {
        NSString *cleanedOption = [selectorName substringWithRange:NSMakeRange(13, selectorName.length - 14)];

        NSString *originalOption = nil;
        for (NSString *option in _menuOptions) {
            NSString *testSelectorName = [[option stringByReplacingOccurrencesOfString:@" " withString:@"_"]
                                                    stringByReplacingOccurrencesOfString:@"[^a-zA-Z0-9_]"
                                                    withString:@"_"
                                                    options:NSRegularExpressionSearch
                                                    range:NSMakeRange(0, option.length)];
            if ([testSelectorName isEqualToString:cleanedOption]) {
                originalOption = option;
                break;
            }
        }

        if (originalOption) {
            [self handleMenuSelection:originalOption];
        }
    } else {
        [super forwardInvocation:anInvocation];
    }
}

- (void)handleMenuSelection:(NSString *)selectedOption
{
    NSRange selectedRange = _textView.selectedRange;
    NSString *selectedText = @"";

    if (selectedRange.location != NSNotFound && selectedRange.length > 0) {
        selectedText = [_textView.text substringWithRange:selectedRange];
    }

    _textView.selectedRange = NSMakeRange(0, 0);
    [[UIMenuController sharedMenuController] hideMenuFromView:_textView];

    if (auto eventEmitter = std::static_pointer_cast<const SelectableTextViewEventEmitter>(_eventEmitter)) {
        SelectableTextViewEventEmitter::OnSelection selectionEvent = {
            .chosenOption = std::string([selectedOption UTF8String]),
            .highlightedText = std::string([selectedText UTF8String])
        };
        eventEmitter->onSelection(selectionEvent);
    }
}

Class<RCTComponentViewProtocol> SelectableTextViewCls(void)
{
    return SelectableTextView.class;
}

@end
